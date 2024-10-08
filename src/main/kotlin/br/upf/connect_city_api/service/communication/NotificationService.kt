package br.upf.connect_city_api.service.communication

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Notification
import br.upf.connect_city_api.repository.NotificationRepository
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val emailService: EmailService?
) {

    @Transactional
    fun sendNotification(
        call: Call,
        notificationType: String,
        recipientEmail: String?,
        status: String = NotificationMessages.NOTIFICATION_STATUS_PENDING
    ) {
        validateNotificationStatus(status)

        val notification = notificationRepository.findByCallIdAndNotificationType(call.id!!, notificationType)
            ?: createNotification(call, notificationType, status)

        if (shouldAttemptNotification(notification)) {
            try {
                sendEmailIfRequired(call, recipientEmail)
                updateNotificationStatus(notification, NotificationMessages.NOTIFICATION_STATUS_SENT)
            } catch (e: Exception) {
                handleNotificationFailure(notification)
            }
        }

        notificationRepository.save(notification)
    }

    private fun createNotification(call: Call, notificationType: String, status: String): Notification {
        return Notification(
            notificationType = notificationType,
            call = call,
            sentAt = LocalDateTime.now(),
            status = status,
            attemptCount = 0
        )
    }

    private fun shouldAttemptNotification(notification: Notification): Boolean {
        return notification.status != NotificationMessages.NOTIFICATION_STATUS_SENT &&
                notification.attemptCount < NotificationMessages.MAX_NOTIFICATION_ATTEMPTS
    }

    private fun sendEmailIfRequired(call: Call, recipientEmail: String?) {
        recipientEmail?.let {
            val dynamicData: Map<String, String> = createEmailDynamicData(call)
            emailService?.send(
                to = recipientEmail,
                subject = "${NotificationMessages.EMAIL_SUBJECT_CALL_NOTIFICATION}${call.subject}",
                templateId = NotificationMessages.EMAIL_TEMPLATE_ID_CALL_NOTIFICATION,
                dynamicData = dynamicData
            )
        }
    }

    private fun createEmailDynamicData(call: Call): Map<String, String> {
        return mapOf(
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_ID to call.id.toString(),
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_SUBJECT to call.subject,
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_DESCRIPTION to call.description,
            NotificationMessages.EMAIL_DYNAMIC_KEY_CALL_STATUS to call.status.toString()
        )
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

    @Transactional
    fun markNotificationAsSent(notificationId: Long) {
        val notification = getNotificationById(notificationId)
        updateNotificationStatus(notification, NotificationMessages.NOTIFICATION_STATUS_SENT)
    }

    @Transactional(readOnly = true)
    fun getPendingNotifications(): List<Notification> {
        return notificationRepository.findByStatus(NotificationMessages.NOTIFICATION_STATUS_PENDING)
    }

    @Transactional(readOnly = true)
    fun getNotificationsForCall(callId: Long): List<Notification> {
        return notificationRepository.findByCallId(callId)
    }

    @Transactional
    fun updateNotificationStatuses(notificationIds: List<Long>, newStatus: String) {
        validateNotificationStatus(newStatus)
        val notifications = notificationRepository.findAllById(notificationIds)
        notifications.forEach { updateNotificationStatus(it, newStatus) }
        notificationRepository.saveAll(notifications)
    }

    @Transactional(readOnly = true)
    fun hasPendingNotificationsForCall(callId: Long): Boolean {
        return notificationRepository.existsByCallIdAndStatus(callId, NotificationMessages.NOTIFICATION_STATUS_PENDING)
    }

    @Transactional
    fun handleFailedNotification(notificationId: Long) {
        val notification = getNotificationById(notificationId)
        handleNotificationFailure(notification)
    }

    @Transactional(readOnly = true)
    private fun getNotificationById(notificationId: Long): Notification {
        return notificationRepository.findById(notificationId)
            .orElseThrow { throw ResourceNotFoundError(NotificationMessages.NOTIFICATION_NOT_FOUND) }
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