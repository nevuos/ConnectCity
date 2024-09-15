package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.user.Admin
import br.upf.connect_city_api.model.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByUser(user: User): Admin?
}