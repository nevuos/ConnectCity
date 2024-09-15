package br.upf.connect_city_api.dtos.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequestDTO(

    @field:Email(message = "O e-mail deve ser válido")
    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Size(max = 50, message = "O e-mail deve ter no máximo 50 caracteres")
    val email: String,

    @field:NotBlank(message = "A senha é obrigatória")
    @field:Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres")
    val password: String
)
