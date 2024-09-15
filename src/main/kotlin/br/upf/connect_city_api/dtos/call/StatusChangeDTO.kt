package br.upf.connect_city_api.dtos.call

import java.time.LocalDateTime

data class StatusChangeDTO(
    val id: Long?,
    val previousStatus: String,
    val newStatus: String,
    val changedAt: LocalDateTime,
    val changedBy: String
)