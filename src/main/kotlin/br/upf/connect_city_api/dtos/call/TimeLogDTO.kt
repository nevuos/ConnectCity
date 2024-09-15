package br.upf.connect_city_api.dtos.call

import java.time.LocalDateTime

data class TimeLogDTO(
    val id: Long?,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime?,
    val employee: String?
)