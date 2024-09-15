package br.upf.connect_city_api.dtos.employee

import br.upf.connect_city_api.util.validation.ValidCPF
import br.upf.connect_city_api.util.validation.ValidPhoneNumber
import jakarta.validation.constraints.Size

data class UpdateMunicipalEmployeeRequestDTO(
    @field:Size(min = 2, max = 50, message = "O primeiro nome deve ter entre 2 e 50 caracteres.")
    val firstName: String? = null,

    @field:Size(min = 2, max = 50, message = "O sobrenome deve ter entre 2 e 50 caracteres.")
    val lastName: String? = null,

    @field:Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres.")
    @field:ValidCPF
    val cpf: String? = null,

    @field:Size(min = 10, max = 15, message = "O número de telefone deve ter entre 10 e 15 caracteres.")
    @field:ValidPhoneNumber
    val phoneNumber: String? = null,

    @field:Size(min = 2, max = 100, message = "O cargo deve ter entre 2 e 100 caracteres.")
    val jobTitle: String? = null,

    @field:Size(min = 2, max = 100, message = "O departamento deve ter entre 2 e 100 caracteres.")
    val department: String? = null
)