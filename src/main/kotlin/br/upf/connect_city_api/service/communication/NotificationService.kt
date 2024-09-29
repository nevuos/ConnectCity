package br.upf.connect_city_api.service.communication

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Notification
import br.upf.connect_city_api.repository.NotificationRepository
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun sendNotification(
        call: Call,
        notificationType: String,
        status: String = NotificationMessages.NOTIFICATION_STATUS_PENDING
    ) {
        validateNotificationStatus(status)
        val notification = Notification(
            notificationType = notificationType,
            call = call,
            sentAt = LocalDateTime.now(),
            status = status
        )
        notificationRepository.save(notification)
    }

    fun markNotificationAsSent(notificationId: Long) {
        val notification = getNotificationById(notificationId)
        updateNotificationStatus(notification, NotificationMessages.NOTIFICATION_STATUS_SENT)
    }

    fun getPendingNotifications(): List<Notification> {
        return notificationRepository.findByStatus(NotificationMessages.NOTIFICATION_STATUS_PENDING)
    }

    fun getNotificationsForCall(callId: Long): List<Notification> {
        return notificationRepository.findByCallId(callId)
    }

    fun updateNotificationStatuses(notificationIds: List<Long>, newStatus: String) {
        validateNotificationStatus(newStatus)
        val notifications = notificationRepository.findAllById(notificationIds)
        notifications.forEach { notification ->
            updateNotificationStatus(notification, newStatus)
        }
        notificationRepository.saveAll(notifications)
    }

    fun hasPendingNotificationsForCall(callId: Long): Boolean {
        return notificationRepository.existsByCallIdAndStatus(callId, NotificationMessages.NOTIFICATION_STATUS_PENDING)
    }

    fun handleFailedNotification(notificationId: Long) {
        val notification = getNotificationById(notificationId)
        updateNotificationStatus(notification, NotificationMessages.NOTIFICATION_STATUS_FAILED)
    }

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

    private fun updateNotificationStatus(notification: Notification, newStatus: String) {
        notification.status = newStatus
        notification.sentAt = LocalDateTime.now()
        notificationRepository.save(notification)
    }
}