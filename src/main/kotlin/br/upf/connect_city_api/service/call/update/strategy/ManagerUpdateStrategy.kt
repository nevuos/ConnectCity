package br.upf.connect_city_api.service.call.update.strategy

import br.upf.connect_city_api.dtos.call.UpdateCallByManagerRequestDTO
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.StatusChange
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.StatusChangeRepository
import br.upf.connect_city_api.service.communication.NotificationService
import br.upf.connect_city_api.service.storage.AttachmentService
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import br.upf.connect_city_api.util.constants.user.UserMessages
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Component
class ManagerUpdateStrategy(
    private val callRepository: CallRepository,
    private val attachmentService: AttachmentService,
    private val notificationService: NotificationService,
    private val statusChangeRepository: StatusChangeRepository
) : UpdateStrategy<UpdateCallByManagerRequestDTO> {

    private val logger = LoggerFactory.getLogger(ManagerUpdateStrategy::class.java)

    override fun updateCall(
        user: User,
        call: Call,
        updateRequest: UpdateCallByManagerRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        logger.info("Iniciando atualização do chamado com ID: ${call.id} pelo usuário: ${user.username}")

        val newStatus = updateRequest.status
        if (newStatus != null && newStatus != call.status) {
            logger.info("Status mudou de ${call.status} para $newStatus")
            handleStatusChange(call, newStatus, user)
        } else {
            logger.info("Nenhuma mudança de status detectada para o chamado com ID: ${call.id}")
        }

        processUpdatesAndAttachments(call, updateRequest, attachments, removeAttachmentIds)

        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }

    private fun handleStatusChange(call: Call, newStatus: CallStatus, user: User) {
        call.status = newStatus
        call.updatedAt = LocalDateTime.now()
        call.updatedBy = getFullNameOrUsername(call.assignedTo?.firstName, call.assignedTo?.lastName, user.username)

        val statusChange = StatusChange(
            status = newStatus,
            changedAt = LocalDateTime.now(),
            changedBy = user.username,
            call = call
        )
        statusChangeRepository.save(statusChange)
        logger.info("Mudança de status salva para o chamado com ID: ${call.id}, Status: $newStatus")

        val email = call.citizen?.user?.email ?: user.email
        val customMessage = getCustomMessageByStatus(newStatus, call)

        notificationService.sendNotification(
            call,
            NotificationMessages.NOTIFICATION_TYPE_CALL,
            email,
            customMessage
        )
        logger.info("Notificação de mudança de status enviada para o email: $email")
    }

    private fun getCustomMessageByStatus(newStatus: CallStatus, call: Call): String {
        return when (newStatus) {
            CallStatus.IN_PROGRESS -> NotificationMessages.EMAIL_MESSAGE_IN_PROGRESS.replace("{CALL_ID}", call.id.toString())
            CallStatus.RESOLVED -> NotificationMessages.EMAIL_MESSAGE_RESOLVED.replace("{CALL_ID}", call.id.toString())
            CallStatus.CLOSED -> NotificationMessages.EMAIL_MESSAGE_CLOSED.replace("{CALL_ID}", call.id.toString())
            else -> NotificationMessages.EMAIL_MESSAGE_GENERIC.replace("{CALL_ID}", call.id.toString()).replace("{STATUS}", newStatus.toString())
        }
    }

    private fun processUpdatesAndAttachments(
        call: Call,
        updateRequest: UpdateCallByManagerRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ) {
        call.description = updateRequest.description ?: call.description
        call.estimatedCompletion = updateRequest.estimatedCompletion ?: call.estimatedCompletion
        call.priority = updateRequest.priority ?: call.priority
        call.updatedAt = LocalDateTime.now()
        call.updatedBy = getFullNameOrUsername(call.assignedTo?.firstName, call.assignedTo?.lastName, call.updatedBy)

        attachmentService.processAttachmentsForCall(call.id!!, attachments, removeAttachmentIds)
        logger.info("Anexos processados para o chamado com ID: ${call.id}")

        callRepository.save(call)
        logger.info("Chamado com ID: ${call.id} salvo com sucesso.")
    }

    private fun getFullNameOrUsername(firstName: String?, lastName: String?, username: String?): String {
        return if (!firstName.isNullOrBlank() && !lastName.isNullOrBlank()) {
            "$firstName $lastName"
        } else {
            username ?: UserMessages.USER_NOT_FOUND
        }
    }
}