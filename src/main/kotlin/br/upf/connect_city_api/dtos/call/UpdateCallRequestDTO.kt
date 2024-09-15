package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UpdateCallRequestDTO(
    var id: Long?,

    var assignedToId: Long?,

    @field:Size(min = 2, max = 255, message = "O assunto deve ter entre 2 e 255 caracteres.")
    val subject: String? = null,

    val description: String? = null,

    val priority: PriorityLevel? = null,

    val tags: List<String>? = null,

    val categoryIds: List<Long>? = null,

    val updatedBy: String? = null,

    val isPublic: Boolean? = null,

    val estimatedCompletion: LocalDateTime? = null,

    val progressPercentage: Int? = null,

    val progressNotes: String? = null
)