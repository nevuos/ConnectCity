package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.model.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface MunicipalEmployeeRepository : JpaRepository<MunicipalEmployee, Long>,
    JpaSpecificationExecutor<MunicipalEmployee> {
    fun findByCpf(cpf: String): MunicipalEmployee?
    fun findByUser(user: User): MunicipalEmployee?
}