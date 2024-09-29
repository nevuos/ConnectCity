package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.Interaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InteractionRepository : JpaRepository<Interaction, Long> {
    fun findByCallId(callId: Long): List<Interaction>
}