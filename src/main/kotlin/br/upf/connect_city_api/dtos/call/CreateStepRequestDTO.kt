package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import java.time.LocalDateTime

data class CreateStepRequestDTO(
    val description: String,
    val priority: PriorityLevel? = PriorityLevel.MEDIUM,
    val endTime: LocalDateTime? = null,
    val notes: String? = null
)
