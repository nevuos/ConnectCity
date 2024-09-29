package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.Step
import br.upf.connect_city_api.model.entity.call.TimeLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TimeLogRepository : JpaRepository<TimeLog, Long> {
    fun findByStepId(stepId: Long): List<TimeLog>
    fun findByCallId(callId: Long): List<TimeLog>
    fun findFirstByStepOrderByLogDateTimeAsc(step: Step): TimeLog?
}