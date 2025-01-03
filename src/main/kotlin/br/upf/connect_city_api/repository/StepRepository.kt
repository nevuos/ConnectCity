package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.Step
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StepRepository : JpaRepository<Step, Long> {
    fun findAllByCallId(callId: Long): List<Step>
}