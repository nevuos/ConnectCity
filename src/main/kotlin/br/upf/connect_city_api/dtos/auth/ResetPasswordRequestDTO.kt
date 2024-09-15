package br.upf.connect_city_api.dtos.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ResetPasswordRequestDTO(
    @field:NotBlank(message = "O token é obrigatório")
    val token: String,

    @field:NotBlank(message = "A nova senha é obrigatória")
    @field:Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres")
    val newPassword: String
)
