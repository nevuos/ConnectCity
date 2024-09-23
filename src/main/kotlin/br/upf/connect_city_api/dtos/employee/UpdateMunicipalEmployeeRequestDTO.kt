package br.upf.connect_city_api.dtos.employee

import br.upf.connect_city_api.util.validation.ValidCPF
import br.upf.connect_city_api.util.validation.ValidPhoneNumber
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateMunicipalEmployeeRequestDTO(
    @field:Size(min = 2, max = 50, message = "O primeiro nome deve ter entre 2 e 50 caracteres.")
    val firstName: String? = null,

    @field:Size(min = 2, max = 50, message = "O sobrenome deve ter entre 2 e 50 caracteres.")
    val lastName: String? = null,

    @field:Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres.")
    @field:ValidCPF
    val cpf: String? = null,

    val dateOfBirth: LocalDate? = null,

    @field:Size(min = 1, max = 10, message = "O gênero deve ter entre 1 e 10 caracteres.")
    val gender: String?,

    @field:ValidPhoneNumber
    val phoneNumber: String? = null,

    @field:Size(min = 2, max = 50, message = "O cargo deve ter entre 2 e 50 caracteres.")
    val jobTitle: String? = null,

    @field:Size(min = 2, max = 50, message = "O departamento deve ter entre 2 e 50 caracteres.")
    val department: String? = null,

    val employeeType: String? = "INTERNAL"
)