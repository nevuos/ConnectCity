package br.upf.connect_city_api.dtos.address

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SimpleAddressDTO(

    @field:NotBlank(message = "O nome da rua é obrigatório.")
    @field:Size(min = 2, max = 100, message = "O nome da rua deve ter entre 2 e 100 caracteres.")
    var street: String? = null,

    @field:Size(min = 1, max = 10, message = "O número deve ter entre 1 e 10 caracteres.")
    var number: String? = null,

    @field:Size(max = 100, message = "O complemento pode ter no máximo 100 caracteres.")
    var complement: String? = null,

    @field:NotBlank(message = "O bairro é obrigatório.")
    @field:Size(min = 2, max = 50, message = "O bairro deve ter entre 2 e 50 caracteres.")
    var neighborhood: String? = null,

    @field:Size(min = 2, max = 50, message = "A cidade deve ter entre 2 e 50 caracteres.")
    var city: String? = null,

    @field:Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres.")
    var state: String? = null,

    @field:Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 12345-678.")
    var postalCode: String? = null,

    @field:Size(min = 2, max = 50, message = "O nome do país deve ter entre 2 e 50 caracteres.")
    var country: String? = "Brazil"
)