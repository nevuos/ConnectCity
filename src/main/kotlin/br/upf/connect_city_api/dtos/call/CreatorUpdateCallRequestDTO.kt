package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.dtos.address.SimpleAddressDTO
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.util.validation.ValidPhoneNumber
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

data class CreatorUpdateCallRequestDTO(
    var id: Long?,

    @field:Size(min = 3, max = 100, message = "O assunto deve ter entre 3 e 100 caracteres.")
    val subject: String? = null,

    @field:Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres.")
    val description: String? = null,

    val isPublic: Boolean? = null,

    val status: CallStatus? = null,

    @field:Valid
    val address: SimpleAddressDTO? = null,

    @field:ValidPhoneNumber
    val phoneNumber: String? = null,

    val language: String? = "pt-BR"
)