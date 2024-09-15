package br.upf.connect_city_api.dtos.user

import br.upf.connect_city_api.model.entity.enums.UserType
import java.time.LocalDateTime

data class UserDetailsDTO(
    val id: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val userType: UserType? = null,
    val createdAt: LocalDateTime? = null
)