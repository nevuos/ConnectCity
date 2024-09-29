package br.upf.connect_city_api.service.storage

import br.upf.connect_city_api.model.entity.call.Attachment
import br.upf.connect_city_api.model.entity.call.Step
import br.upf.connect_city_api.repository.AttachmentRepository
import br.upf.connect_city_api.util.constants.storage.AttachmentMessages
import br.upf.connect_city_api.util.constants.storage.StorageConstants
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture

@Service
class AttachmentService(
    private val storageService: StorageService,
    private val attachmentRepository: AttachmentRepository
) {

    private val logger = LoggerFactory.getLogger(AttachmentService::class.java)

    fun processAttachments(
        entityWithAttachments: MutableList<Attachment>,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>? = null,
        step: Step? = null
    ) {
        removeAttachments(entityWithAttachments, removeAttachmentIds)

        if (!attachments.isNullOrEmpty()) {
            val uploadFutures = attachments.map { file ->
                storageService.uploadFileAsync(file).thenApply { storedFilePath ->
                    val attachment = createAttachment(file, storedFilePath, step)
                    entityWithAttachments.add(attachment)
                    logger.info(AttachmentMessages.ATTACHMENT_UPLOAD_SUCCESS)
                }.exceptionally {
                    logger.error("${AttachmentMessages.ATTACHMENT_UPLOAD_FAILED}: ${it.message}")
                    throw it
                }
            }
            CompletableFuture.allOf(*uploadFutures.toTypedArray()).join()
            attachmentRepository.saveAll(entityWithAttachments)
            logger.info(AttachmentMessages.ATTACHMENT_PROCESS_COMPLETED)
        }
    }

    private fun removeAttachments(entityWithAttachments: MutableList<Attachment>, removeAttachmentIds: List<Long>?) {
        removeAttachmentIds?.let { idsToRemove ->
            val attachmentsToRemove = entityWithAttachments.filter { it.id in idsToRemove }
            entityWithAttachments.removeAll(attachmentsToRemove)

            attachmentsToRemove.forEach { attachment ->
                storageService.deleteFile(attachment.fileUrl).exceptionally {
                    logger.error("${AttachmentMessages.ATTACHMENT_REMOVAL_FAILED}: ${it.message}")
                    throw it
                }
                attachmentRepository.delete(attachment)
            }
        }
    }

    fun deleteAttachmentById(attachmentId: Long) {
        val attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow {
                logger.error(AttachmentMessages.ATTACHMENT_NOT_FOUND)
                throw ResourceNotFoundError(AttachmentMessages.ATTACHMENT_NOT_FOUND)
            }

        storageService.deleteFile(attachment.fileUrl).exceptionally {
            logger.error("${AttachmentMessages.ATTACHMENT_REMOVAL_FAILED}: ${it.message}")
            throw it
        }

        attachmentRepository.delete(attachment)
        logger.info(AttachmentMessages.ATTACHMENT_DELETED_SUCCESS)
    }

    fun getAttachmentById(attachmentId: Long): Attachment {
        return attachmentRepository.findById(attachmentId)
            .orElseThrow {
                logger.error(AttachmentMessages.ATTACHMENT_NOT_FOUND)
                throw ResourceNotFoundError(AttachmentMessages.ATTACHMENT_NOT_FOUND)
            }
    }

    private fun createAttachment(file: MultipartFile, storedFilePath: String, step: Step?): Attachment {
        return Attachment(
            step = step,
            fileName = file.originalFilename ?: StorageConstants.DEFAULT_FILE_NAME,
            fileUrl = storedFilePath,
            version = 1
        )
    }
}