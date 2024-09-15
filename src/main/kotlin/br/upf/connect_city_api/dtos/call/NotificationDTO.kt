package br.upf.connect_city_api.br.upf.connect_city_api.dtos.call

import java.time.LocalDateTime

data class NotificationDTO(
    val id: Long?,
    val message: String,
    val sentAt: LocalDateTime,
    val recipient: String
)