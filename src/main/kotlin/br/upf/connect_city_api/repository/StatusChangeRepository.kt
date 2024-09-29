package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.StatusChange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StatusChangeRepository : JpaRepository<StatusChange, Long>