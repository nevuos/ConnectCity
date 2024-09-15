package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import java.time.LocalDateTime

data class CallDTO(
    val id: Long?,
    val subject: String,
    val status: CallStatus,
    val priority: PriorityLevel,
    val createdAt: LocalDateTime,
    val estimatedCompletion: LocalDateTime?
)