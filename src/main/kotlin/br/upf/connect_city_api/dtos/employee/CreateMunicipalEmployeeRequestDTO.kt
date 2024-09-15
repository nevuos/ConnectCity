package br.upf.connect_city_api.dtos.employee

import br.upf.connect_city_api.util.validation.ValidCPF
import br.upf.connect_city_api.util.validation.ValidPhoneNumber
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateMunicipalEmployeeRequestDTO(
    @field:NotBlank(message = "O primeiro nome é obrigatório.")
    @field:Size(min = 2, max = 50, message = "O primeiro nome deve ter entre 2 e 50 caracteres.")
    val firstName: String,

    @field:NotBlank(message = "O sobrenome é obrigatório.")
    @field:Size(min = 2, max = 50, message = "O sobrenome deve ter entre 2 e 50 caracteres.")
    val lastName: String,

    @field:NotBlank(message = "O CPF é obrigatório.")
    @field:Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres.")
    @field:ValidCPF
    val cpf: String,

    @field:NotBlank(message = "O número de telefone é obrigatório.")
    @field:Size(min = 10, max = 15, message = "O número de telefone deve ter entre 10 e 15 caracteres.")
    @field:ValidPhoneNumber
    val phoneNumber: String,

    @field:NotBlank(message = "O cargo é obrigatório.")
    @field:Size(min = 2, max = 100, message = "O cargo deve ter entre 2 e 100 caracteres.")
    val jobTitle: String,

    @field:NotBlank(message = "O departamento é obrigatório.")
    @field:Size(min = 2, max = 100, message = "O departamento deve ter entre 2 e 100 caracteres.")
    val department: String
)