package br.upf.connect_city_api.dtos.email

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


data class EmailRequestDTO(
    @NotBlank(message = "O campo 'to' não pode estar em branco.")
    val to: String,

    @NotBlank(message = "O campo 'subject' não pode estar em branco.")
    val subject: String,

    @Email(message = "O campo 'confirmationUrl' deve ser um endereço de e-mail válido.")
    @NotBlank(message = "O campo 'confirmationUrl' não pode estar em branco.")
    val confirmationUrl: String,

    @NotBlank(message = "O campo 'templateId' não pode estar em branco.")
    val templateId: String
)

