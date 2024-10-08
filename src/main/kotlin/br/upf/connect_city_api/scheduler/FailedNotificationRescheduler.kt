package br.upf.connect_city_api.scheduler

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.repository.NotificationRepository
import br.upf.connect_city_api.repository.CitizenRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import br.upf.connect_city_api.service.communication.NotificationService
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FailedNotificationRescheduler(
    private val notificationService: NotificationService,
    private val notificationRepository: NotificationRepository,
    private val citizenRepository: CitizenRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository
) {

    @Scheduled(fixedDelay = 60000)
    @Transactional
    fun resendFailedNotifications() {
        val failedNotifications = notificationRepository.findByStatus(NotificationMessages.NOTIFICATION_STATUS_FAILED)

        failedNotifications.forEach { notification ->
            if (notification.attemptCount < NotificationMessages.MAX_NOTIFICATION_ATTEMPTS) {
                try {
                    val recipientEmail = getCreatorEmail(notification.call)

                    recipientEmail?.let {
                        notificationService.sendNotification(
                            notification.call,
                            notification.notificationType,
                            recipientEmail
                        )
                    }

                } catch (e: Exception) {
                    notification.attemptCount++
                    notificationRepository.save(notification)
                }
            }
        }
    }

    private fun getCreatorEmail(call: Call): String? {
        call.citizen?.id?.let { citizenId ->
            val citizen = citizenRepository.findById(citizenId)
            if (citizen.isPresent) {
                return citizen.get().user.email
            }
        }
        call.employee?.id?.let { employeeId ->
            val employee = municipalEmployeeRepository.findById(employeeId)
            if (employee.isPresent) {
                return employee.get().user.email
            }
        }
        return null
    }
}