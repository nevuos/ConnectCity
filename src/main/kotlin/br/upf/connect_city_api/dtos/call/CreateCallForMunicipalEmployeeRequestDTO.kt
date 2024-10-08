package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.dtos.address.SimpleAddressDTO
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.util.validation.ValidPhoneNumber
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateCallForMunicipalEmployeeRequestDTO(

    @field:NotBlank(message = "O assunto é obrigatório.")
    @field:Size(min = 3, max = 100, message = "O assunto deve ter entre 3 e 100 caracteres.")
    val subject: String,

    @field:NotBlank(message = "A descrição é obrigatória.")
    @field:Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres.")
    val description: String,

    @field:Valid
    val address: SimpleAddressDTO,

    val priority: PriorityLevel = PriorityLevel.MEDIUM,

    val categoryIds: List<Long>? = null,

    @field:ValidPhoneNumber
    val phoneNumber: String? = null,

    val isPublic: Boolean? = true,

    val language: String? = "pt-BR",

    val tags: List<String>? = null,

    val assignedToEmployeeId: Long? = null,

    val escalationLevel: Int? = 0,

    val predecessorCallIds: List<Long>? = null,

    val successorCallIds: List<Long>? = null,
)