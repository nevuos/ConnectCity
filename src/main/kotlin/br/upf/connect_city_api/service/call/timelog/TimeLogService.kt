package br.upf.connect_city_api.service.call.timelog

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Step
import br.upf.connect_city_api.model.entity.call.TimeLog
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.repository.TimeLogRepository
import br.upf.connect_city_api.util.constants.logging.TimeLogMessages
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
class TimeLogService(
    private val timeLogRepository: TimeLogRepository
) {
    private val logger = LoggerFactory.getLogger(TimeLogService::class.java)

    @Transactional
    fun logStepStart(step: Step, employee: MunicipalEmployee, call: Call) {
        createAndSaveTimeLog(step, call, employee, TimeLogMessages.STEP_STARTED_LOG)
    }

    @Transactional
    fun logStepCompletion(step: Step, employee: MunicipalEmployee) {
        val startLog = getStartLogForStep(step)
        val timeSpentMinutes = calculateTimeDifferenceInMinutes(startLog.logDateTime, LocalDateTime.now())

        createAndSaveTimeLog(step, step.call, employee, TimeLogMessages.STEP_COMPLETED_LOG, timeSpentMinutes)
    }

    @Transactional
    fun logCallCompletion(call: Call, employee: MunicipalEmployee) {
        val totalTimeSpent = calculateTotalTimeSpentForCall(call.id!!)
        createAndSaveTimeLog(call.steps.last(), call, employee, TimeLogMessages.CALL_COMPLETED_LOG, totalTimeSpent)
    }

    @Transactional
    fun calculateTotalTimeSpentForStep(stepId: Long): Int {
        val totalTimeSpent = timeLogRepository.findByStepId(stepId).sumOf { it.timeSpentMinutes }
        logger.info(TimeLogMessages.TOTAL_TIME_SPENT_FOR_STEP, stepId, totalTimeSpent)
        return totalTimeSpent
    }

    @Transactional
    fun calculateTotalTimeSpentForCall(callId: Long): Int {
        val totalTimeSpent = timeLogRepository.findByCallId(callId).sumOf { it.timeSpentMinutes }
        logger.info(TimeLogMessages.TOTAL_TIME_SPENT_FOR_CALL, callId, totalTimeSpent)
        return totalTimeSpent
    }

    private fun createAndSaveTimeLog(
        step: Step,
        call: Call,
        employee: MunicipalEmployee,
        description: String,
        timeSpentMinutes: Int? = 0
    ) {
        val timeLog = TimeLog(
            employee = employee,
            step = step,
            call = call,
            timeSpentMinutes = timeSpentMinutes ?: 0,
            logDateTime = LocalDateTime.now(),
            description = description
        )
        timeLogRepository.save(timeLog)
        logger.info(TimeLogMessages.TIME_LOG_CREATED, step.id, employee.id)
    }


    private fun getStartLogForStep(step: Step): TimeLog {
        return timeLogRepository.findFirstByStepOrderByLogDateTimeAsc(step)
            ?: throw IllegalStateException(TimeLogMessages.NO_START_LOG_FOUND.format(step.id))
    }

    private fun calculateTimeDifferenceInMinutes(startTime: LocalDateTime, endTime: LocalDateTime): Int {
        return Duration.between(startTime, endTime).toMinutes().toInt()
    }
}