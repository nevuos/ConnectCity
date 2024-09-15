package br.upf.connect_city_api.dtos.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequestDTO(

    @field:NotBlank(message = "O nome de usuário é obrigatório")
    @field:Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres")
    val username: String,

    @field:NotBlank(message = "A senha é obrigatória")
    @field:Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres")
    val password: String,

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "O e-mail deve ser válido")
    @field:Size(max = 50, message = "O e-mail deve ter no máximo 50 caracteres")
    val email: String,

    val emailConfirmed: Boolean = false
)