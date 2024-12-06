package br.upf.connect_city_api.service.communication

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Notification
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.NotificationRepository
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val emailService: EmailService?,
    private val callRepository: CallRepository
) {

    @Transactional
    fun sendNotification(
        call: Call,
        notificationType: String,
        recipientEmail: String?,
        customMessage: String?,
        status: String = NotificationMessages.NOTIFICATION_STATUS_PENDING
    ) {
        validateNotificationStatus(status)

        val notification = notificationRepository.findByCallIdAndNotificationType(call.id!!, notificationType)
            ?: createNotification(call, notificationType, status)

        if (shouldAttemptNotification(notification)) {
            try {
                val dynamicData = createEmailDynamicData(call, customMessage)
                sendEmailIfRequired(recipientEmail, dynamicData)
                updateNotificationStatus(notification, NotificationMessages.NOTIFICATION_STATUS_SENT)
                notification.message = customMessage
                notification.recipient = recipientEmail
                updateLastNotifiedAt(call)
            } catch (e: Exception) {
                handleNotificationFailure(notification)
            }
        }

        notificationRepository.save(notification)
    }

    private fun createEmailDynamicData(call: Call, customMessage: String?): Map<String, String> {
        val isNewCall = isNewCall(call)

        val message = if (isNewCall) {
            NotificationMessages.EMAIL_OPENING_MESSAGE
        } else {
            NotificationMessages.EMAIL_STATUS_UPDATE_MESSAGE
        }

        return mapOf(
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_ID to call.id.toString(),
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_SUBJECT to call.subject,
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_DESCRIPTION to call.description,
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_STATUS to call.status.toString(),
            NotificationMessages.EMAIL_DYNAMIC_KEY_CUSTOM_MESSAGE to (customMessage ?: message)
        )
    }

    private fun isNewCall(call: Call): Boolean {
        return call.lastNotifiedAt == null
    }

    private fun sendEmailIfRequired(
        recipientEmail: String?,
        dynamicData: Map<String, String>
    ) {
        recipientEmail?.let {
            emailService?.send(
                to = it,
                subject = NotificationMessages.EMAIL_SUBJECT_CALL_NOTIFICATION + dynamicData[NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_SUBJECT],
                templateId = NotificationMessages.EMAIL_TEMPLATE_ID_CALL_NOTIFICATION,
                dynamicData = dynamicData
            )
        }
    }

    private fun createNotification(call: Call, notificationType: String, status: String): Notification {
        val message = NotificationMessages.CALL_CREATED_MESSAGE_CITIZEN.replace("{CALL_ID}", call.id.toString())
        return Notification(
            notificationType = notificationType,
            call = call,
            sentAt = LocalDateTime.now(),
            status = status,
            message = message,
            recipient = call.citizen?.user?.email ?: call.employee?.user?.email
        )
    }

    private fun shouldAttemptNotification(notification: Notification): Boolean {
        return notification.status != NotificationMessages.NOTIFICATION_STATUS_SENT &&
                notification.attemptCount < NotificationMessages.MAX_NOTIFICATION_ATTEMPTS
    }

    private fun handleNotificationFailure(notification: Notification) {
        notification.status = NotificationMessages.NOTIFICATION_STATUS_FAILED
        notification.attemptCount++
        notification.sentAt = LocalDateTime.now()
    }

    private fun updateNotificationStatus(notification: Notification, newStatus: String) {
        notification.status = newStatus
        notification.sentAt = LocalDateTime.now()
    }

    private fun updateLastNotifiedAt(call: Call) {
        call.lastNotifiedAt = LocalDateTime.now()
        callRepository.save(call)
    }

    @Transactional(readOnly = true)
    private fun getNotificationById(notificationId: Long): Notification {
        return notificationRepository.findById(notificationId)
            .orElseThrow { ResourceNotFoundError(NotificationMessages.NOTIFICATION_NOT_FOUND) }
    }

    private fun validateNotificationStatus(status: String) {
        val validStatuses = listOf(
            NotificationMessages.NOTIFICATION_STATUS_PENDING,
            NotificationMessages.NOTIFICATION_STATUS_SENT,
            NotificationMessages.NOTIFICATION_STATUS_FAILED
        )
        if (status !in validStatuses) {
            throw IllegalArgumentException(NotificationMessages.INVALID_NOTIFICATION_STATUS)
        }
    }
}