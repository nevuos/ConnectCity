package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {

    fun findByStatus(status: String): List<Notification>

    fun findByCallIdAndNotificationType(callId: Long, notificationType: String): Notification?

    fun findByCallId(callId: Long): List<Notification>

    fun existsByCallIdAndStatus(callId: Long, status: String): Boolean
}