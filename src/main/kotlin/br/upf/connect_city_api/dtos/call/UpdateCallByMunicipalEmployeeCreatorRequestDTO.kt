package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.dtos.address.SimpleAddressDTO
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.util.validation.ValidPhoneNumber
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

data class UpdateCallByMunicipalEmployeeCreatorRequestDTO(

    @field:Size(min = 3, max = 100, message = "O assunto deve ter entre 3 e 100 caracteres.")
    val subject: String? = null,

    @field:Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres.")
    val description: String? = null,

    @field:Valid
    val address: SimpleAddressDTO? = null,

    val priority: PriorityLevel? = null,

    val categoryIds: List<Long>? = null,

    @field:ValidPhoneNumber
    val phoneNumber: String? = null,

    val isPublic: Boolean? = null,

    val language: String? = "pt-BR",

    val tags: List<String>? = null,

    val assignedToEmployeeId: Long? = null,

    val escalationLevel: Int? = null,

    val predecessorCallIds: List<Long>? = null,

    val successorCallIds: List<Long>? = null
)