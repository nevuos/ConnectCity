package br.upf.connect_city_api.dtos.category

import jakarta.validation.constraints.Size

data class UpdateCategoryRequestDTO(
    @field:Size(min = 2, max = 50, message = "O nome da categoria deve ter entre 2 e 50 caracteres.")
    val name: String,

    val parentCategoryId: Long?
)