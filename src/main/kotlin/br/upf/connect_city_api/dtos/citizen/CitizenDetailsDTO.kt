package br.upf.connect_city_api.dtos.citizen

import br.upf.connect_city_api.dtos.address.AddressDTO
import java.time.LocalDate

data class CitizenDetailsDTO(
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val cpf: String? = null,
    val dateOfBirth: LocalDate? = null,
    val gender: String? = null,
    val phoneNumber: String? = null,
    val address: AddressDTO? = null
)