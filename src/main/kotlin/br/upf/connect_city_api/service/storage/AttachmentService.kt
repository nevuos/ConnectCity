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
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundError(AttachmentMessages.CALL_NOT_FOUND) }

        val entityAttachments = call.attachments.toMutableList()

        processAttachments(entityAttachments, attachments, removeAttachmentIds, call, null)
    }

    @Transactional
    fun processAttachmentsForStep(
        stepId: Long,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>? = null
    ) {
        val step = stepRepository.findById(stepId)
            .orElseThrow { ResourceNotFoundError(AttachmentMessages.STEP_NOT_FOUND) }

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
        removeAttachments(entityAttachments, removeAttachmentIds)
        if (!attachments.isNullOrEmpty()) {
            val uploadFutures = attachments.map { file ->
                storageService.uploadFileAsync(file).thenApply { storedFilePath ->
                    val attachment = createAttachment(file, storedFilePath, call, step)
                    synchronized(entityAttachments) {
                        entityAttachments.add(attachment)
                    }
                    logger.info("${AttachmentMessages.ATTACHMENT_UPLOAD_SUCCESS} - Arquivo: ${file.originalFilename}")
                }.exceptionally { ex ->
                    logger.error("${AttachmentMessages.ATTACHMENT_UPLOAD_FAILED}: ${ex.message}")
                }
            }
            CompletableFuture.allOf(*uploadFutures.toTypedArray()).whenComplete { _, ex ->
                if (ex == null) {
                    attachmentRepository.saveAll(entityAttachments)
                    logger.info(AttachmentMessages.ATTACHMENT_PROCESS_COMPLETED)
                } else {
                    logger.error("${AttachmentMessages.ATTACHMENT_PROCESS_FAILED}: ${ex.message}")
                }
            }
        }
    }

    private fun removeAttachments(entityAttachments: MutableList<Attachment>, removeAttachmentIds: List<Long>?) {
        removeAttachmentIds?.let { idsToRemove ->
            val attachmentsToRemove = entityAttachments.filter { it.id in idsToRemove }
            entityAttachments.removeAll(attachmentsToRemove)

            attachmentsToRemove.forEach { attachment ->
                storageService.deleteFile(attachment.fileUrl).exceptionally { ex ->
                    logger.error("${AttachmentMessages.ATTACHMENT_REMOVAL_FAILED}: ${ex.message}")
                    null
                }
                attachmentRepository.delete(attachment)
            }
            logger.info("${AttachmentMessages.ATTACHMENT_REMOVAL_SUCCESS} - IDs: $idsToRemove")
        }
    }

    @Transactional
    fun deleteAttachmentById(attachmentId: Long) {
        val attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow {
                logger.error(AttachmentMessages.ATTACHMENT_NOT_FOUND)
                ResourceNotFoundError(AttachmentMessages.ATTACHMENT_NOT_FOUND)
            }

        storageService.deleteFile(attachment.fileUrl).exceptionally { ex ->
            logger.error("${AttachmentMessages.ATTACHMENT_REMOVAL_FAILED}: ${ex.message}")
            null
        }

        attachmentRepository.delete(attachment)
        logger.info(AttachmentMessages.ATTACHMENT_REMOVAL_SUCCESS)
    }

    @Transactional(readOnly = true)
    fun getAttachmentById(attachmentId: Long): Attachment {
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
        return Attachment(
            call = call,
            step = step,
            fileName = file.originalFilename ?: StorageConstants.DEFAULT_FILE_NAME,
            fileUrl = storedFilePath,
            version = 1
        )
    }
}