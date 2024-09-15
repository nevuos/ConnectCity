package br.upf.connect_city_api.dtos.citizen

import br.upf.connect_city_api.dtos.address.AddressDTO
import br.upf.connect_city_api.util.validation.ValidCPF
import br.upf.connect_city_api.util.validation.ValidPhoneNumber
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateCitizenRequestDTO(
    @field:Size(min = 2, max = 50, message = "O primeiro nome deve ter entre 2 e 50 caracteres.")
    val firstName: String?,

    @field:Size(min = 2, max = 50, message = "O sobrenome deve ter entre 2 e 50 caracteres.")
    val lastName: String?,

    @field:Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres.")
    @field:ValidCPF
    val cpf: String?,

    val dateOfBirth: LocalDate?,

    @field:Size(min = 1, max = 10, message = "O gênero deve ter entre 1 e 10 caracteres.")
    val gender: String?,

    @field:Size(min = 10, max = 16, message = "O número de telefone deve ter entre 10 e 16 caracteres.")
    @field:ValidPhoneNumber
    val phoneNumber: String?,

    @field:Valid
    val address: AddressDTO?
)