package br.upf.connect_city_api.service.storage

import br.upf.connect_city_api.model.entity.call.Attachment
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Step
import br.upf.connect_city_api.repository.AttachmentRepository
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.StepRepository
import br.upf.connect_city_api.util.constants.storage.AttachmentMessages
import br.upf.connect_city_api.util.constants.storage.StorageConstants
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture

@Service
class AttachmentService(
    private val storageService: StorageService,
    private val attachmentRepository: AttachmentRepository,
    private val callRepository: CallRepository,
    private val stepRepository: StepRepository
) {

    private val logger = LoggerFactory.getLogger(AttachmentService::class.java)

    @Transactional
    fun processAttachmentsForCall(
        callId: Long,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>? = null
    ) {
        logger.info("Iniciando o processamento de anexos para o chamado com ID: $callId")

        val call = callRepository.findById(callId)
            .orElseThrow {
                logger.error("Erro: Chamado não encontrado com ID: $callId")
                ResourceNotFoundError(AttachmentMessages.CALL_NOT_FOUND)
            }

        val entityAttachments = call.attachments.toMutableList()

        processAttachments(entityAttachments, attachments, removeAttachmentIds, call, null)
    }

    @Transactional
    fun processAttachmentsForStep(
        stepId: Long,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>? = null
    ) {
        logger.info("Iniciando o processamento de anexos para o passo com ID: $stepId")

        val step = stepRepository.findById(stepId)
            .orElseThrow {
                logger.error("Erro: Passo não encontrado com ID: $stepId")
                ResourceNotFoundError(AttachmentMessages.STEP_NOT_FOUND)
            }

        val entityAttachments = step.attachments.toMutableList()

        processAttachments(entityAttachments, attachments, removeAttachmentIds, null, step)
    }

    private fun processAttachments(
        entityAttachments: MutableList<Attachment>,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?,
        call: Call?,
        step: Step?
    ) {
        logger.info("Processando anexos. Quantidade de anexos para remover: ${removeAttachmentIds?.size ?: 0}")
        removeAttachments(entityAttachments, removeAttachmentIds)

        if (!attachments.isNullOrEmpty()) {
            logger.info("Iniciando upload de anexos. Quantidade de arquivos para upload: ${attachments.size}")
            val uploadFutures = attachments.map { file ->
                storageService.uploadFileAsync(file).thenApply { storedFilePath ->
                    val attachment = createAttachment(file, storedFilePath, call, step)
                    synchronized(entityAttachments) {
                        entityAttachments.add(attachment)
                    }
                    logger.info(AttachmentMessages.ATTACHMENT_UPLOAD_SUCCESS.format(file.originalFilename))
                }.exceptionally { ex ->
                    logger.error(AttachmentMessages.ATTACHMENT_UPLOAD_FAILED.format(ex.message))
                }
            }

            CompletableFuture.allOf(*uploadFutures.toTypedArray()).whenComplete { _, ex ->
                if (ex == null) {
                    attachmentRepository.saveAll(entityAttachments)
                    logger.info(AttachmentMessages.ATTACHMENT_PROCESS_COMPLETED)
                } else {
                    logger.error(AttachmentMessages.ATTACHMENT_PROCESS_FAILED.format(ex.message))
                }
            }
        } else {
            logger.info("Nenhum arquivo para upload.")
        }
    }

    private fun removeAttachments(entityAttachments: MutableList<Attachment>, removeAttachmentIds: List<Long>?) {
        removeAttachmentIds?.let { idsToRemove ->
            val attachmentsToRemove = entityAttachments.filter { it.id in idsToRemove }
            entityAttachments.removeAll(attachmentsToRemove)
            logger.info("Iniciando remoção de anexos. Quantidade de anexos a remover: ${attachmentsToRemove.size}")

            attachmentsToRemove.forEach { attachment ->
                logger.info("Removendo anexo com ID: ${attachment.id} e URL: ${attachment.fileUrl}")

                storageService.deleteFile(attachment.fileUrl).thenRun {
                    attachmentRepository.delete(attachment)
                    logger.info("${AttachmentMessages.ATTACHMENT_REMOVAL_SUCCESS} - ID: ${attachment.id}")
                }.exceptionally { ex ->
                    logger.error("${AttachmentMessages.ATTACHMENT_REMOVAL_FAILED}: ${ex.message}")
                    null
                }
            }
        } ?: logger.info("Nenhum anexo marcado para remoção.")
    }

    @Transactional(readOnly = true)
    fun getAttachmentById(attachmentId: Long): Attachment {
        logger.info("Buscando anexo com ID: $attachmentId")
        return attachmentRepository.findById(attachmentId)
            .orElseThrow {
                logger.error(AttachmentMessages.ATTACHMENT_NOT_FOUND)
                ResourceNotFoundError(AttachmentMessages.ATTACHMENT_NOT_FOUND)
            }
    }

    private fun createAttachment(
        file: MultipartFile,
        storedFilePath: String,
        call: Call?,
        step: Step?
    ): Attachment {
        logger.info("Criando novo anexo para o arquivo: ${file.originalFilename}, caminho salvo: $storedFilePath")
        return Attachment(
            call = call,
            step = step,
            fileName = file.originalFilename ?: StorageConstants.DEFAULT_FILE_NAME,
            fileUrl = storedFilePath,
            version = 1
        )
    }
}