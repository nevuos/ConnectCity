package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import java.time.LocalDateTime

data class UpdateStepRequestDTO(
    val description: String? = null,
    val priority: PriorityLevel? = null,
    val endTime: LocalDateTime? = null,
    val notes: String? = null
)