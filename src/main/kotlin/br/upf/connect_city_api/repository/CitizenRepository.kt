package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.model.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CitizenRepository : JpaRepository<Citizen, Long>, JpaSpecificationExecutor<Citizen> {
    fun findByCpf(cpf: String): Citizen?
    fun findByUser(user: User): Citizen?
    fun findByPhoneNumber(phoneNumber: String): Citizen?
}