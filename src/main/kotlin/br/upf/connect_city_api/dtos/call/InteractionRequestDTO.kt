package br.upf.connect_city_api.dtos.call

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class InteractionRequestDTO(

    @field:NotBlank(message = "O campo 'updatedBy' é obrigatório.")
    val updatedBy: String,

    @field:NotBlank(message = "O campo 'updateDetails' é obrigatório.")
    @field:Size(max = 1000, message = "Os detalhes da atualização devem ter no máximo 1000 caracteres.")
    val updateDetails: String
)