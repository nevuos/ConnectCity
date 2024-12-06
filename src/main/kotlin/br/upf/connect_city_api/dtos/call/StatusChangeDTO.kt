package br.upf.connect_city_api.dtos.call

import java.time.LocalDateTime

data class StatusChangeDTO(
    val id: Long? = null,
    val previousStatus: String? = null,
    val status: String? = null,
    val changedAt: LocalDateTime? = null,
    val changedBy: String? = null
)