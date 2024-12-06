package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import java.time.LocalDateTime

data class CallDTO(
    val id: Long? = null,
    val subject: String? = null,
    val status: CallStatus? = null,
    val priority: PriorityLevel? = null,
    val createdAt: LocalDateTime? = null,
    val estimatedCompletion: LocalDateTime? = null
)