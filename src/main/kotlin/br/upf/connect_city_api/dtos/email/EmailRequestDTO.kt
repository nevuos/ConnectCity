package br.upf.connect_city_api.dtos.email

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class EmailRequestDTO(

    @Email(message = "O campo 'to' deve ser um endereço de e-mail válido.")
    @NotBlank(message = "O campo 'to' não pode estar em branco.")
    val to: String,

    @NotBlank(message = "O campo 'subject' não pode estar em branco.")
    val subject: String,

    @URL(message = "O campo 'confirmationUrl' deve ser uma URL válida.")
    val confirmationUrl: String? = null,

    @NotBlank(message = "O campo 'templateId' não pode estar em branco.")
    val templateId: String,

    val dynamicData: Map<String, String> = emptyMap()
)