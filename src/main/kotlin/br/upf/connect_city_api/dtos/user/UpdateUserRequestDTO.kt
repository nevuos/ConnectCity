package br.upf.connect_city_api.dtos.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateUserRequestDTO(

    @field:Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres")
    val username: String?,

    @field:Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres")
    val password: String?,

    @field:Email(message = "O e-mail deve ser válido")
    @field:Size(min = 5, max = 50, message = "O e-mail deve ter entre 5 e 50 caracteres")
    val email: String?,
)
