package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.Call
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface CallRepository : JpaRepository<Call, Long>, JpaSpecificationExecutor<Call>