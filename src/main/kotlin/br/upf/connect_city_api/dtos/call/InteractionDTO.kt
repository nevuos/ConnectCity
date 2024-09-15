package br.upf.connect_city_api.dtos.call

import java.time.LocalDateTime

data class InteractionDTO(
    val id: Long?,
    val message: String,
    val createdBy: String,
    val createdAt: LocalDateTime
)