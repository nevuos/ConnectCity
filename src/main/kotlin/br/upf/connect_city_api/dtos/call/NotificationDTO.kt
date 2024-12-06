package br.upf.connect_city_api.dtos.call

import java.time.LocalDateTime

data class NotificationDTO(
    val id: Long? = null,
    val message: String? = null,
    val sentAt: LocalDateTime? = null,
    val recipient: String? = null
)