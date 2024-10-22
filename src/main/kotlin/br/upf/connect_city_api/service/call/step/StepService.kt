package br.upf.connect_city_api.service.call.step


import br.upf.connect_city_api.dtos.call.CreateStepRequestDTO
import br.upf.connect_city_api.dtos.call.UpdateStepRequestDTO
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Step
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.StepRepository
import br.upf.connect_city_api.service.call.timelog.TimeLogService
import br.upf.connect_city_api.service.storage.AttachmentService
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.modelmapper.ModelMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class StepService(
    private val stepRepository: StepRepository,
    private val modelMapper: ModelMapper,
    private val attachmentService: AttachmentService,
    private val timeLogService: TimeLogService,
    private val callRepository: CallRepository
) {

    private val logger = LoggerFactory.getLogger(StepService::class.java)

    @Transactional
    fun create(
        callId: Long,
        createStepRequestDTO: CreateStepRequestDTO,
        attachments: List<MultipartFile>?
    ): String {
        val call = callRepository.findById(callId).orElseThrow {
            throw ResourceNotFoundError(CallMessages.CALL_NOT_FOUND)
        }
        val assignedEmployee = verifyEmployeeAssignedToCall(call)

        val step = modelMapper.map(createStepRequestDTO, Step::class.java).apply {
            this.call = call
            this.assignedTo = assignedEmployee
            this.startTime = LocalDateTime.now()
        }

        stepRepository.save(step)
        attachmentService.processAttachmentsForStep(step.id!!, attachments)

        timeLogService.logStepStart(step, assignedEmployee, call)

        logger.info("Step created successfully for callId: ${call.id} by employee: ${assignedEmployee.id}")
        return CallMessages.STEP_ADDED_SUCCESSFULLY
    }

    @Transactional
    fun update(
        stepId: Long,
        updateStepRequestDTO: UpdateStepRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        val step = getStepByIdAndVerifyAssignedEmployee(stepId)

        modelMapper.map(updateStepRequestDTO, step)
        attachmentService.processAttachmentsForStep(stepId, attachments, removeAttachmentIds)

        stepRepository.save(step)
        logger.info("Step updated successfully with stepId: $stepId")
        return CallMessages.STEP_UPDATED_SUCCESSFULLY
    }

    @Transactional
    fun delete(stepId: Long): String {
        val step = getStepByIdAndVerifyAssignedEmployee(stepId)

        stepRepository.delete(step)
        logger.info("Step deleted successfully with stepId: $stepId")
        return CallMessages.STEP_DELETED_SUCCESSFULLY
    }

    fun getById(stepId: Long): Step {
        return getStepByIdAndVerifyAssignedEmployee(stepId)
    }

    fun getAllForCall(call: Call): List<Step> {
        if (call.id == null) {
            throw InvalidRequestError(CallMessages.CALL_ID_CANNOT_BE_NULL)
        }
        return stepRepository.findAllByCallId(call.id!!)
    }

    private fun verifyEmployeeAssignedToCall(call: Call): MunicipalEmployee {
        return call.assignedTo ?: throw PermissionDeniedError(CallMessages.STEP_ACCESS_DENIED)
    }

    private fun getStepByIdAndVerifyAssignedEmployee(stepId: Long): Step {
        val step = stepRepository.findById(stepId)
            .orElseThrow { throw ResourceNotFoundError(CallMessages.STEP_NOT_FOUND) }

        if (step.assignedTo?.id != step.call.assignedTo?.id) {
            logger.warn("Unauthorized access to stepId: $stepId")
            throw PermissionDeniedError(CallMessages.STEP_ACCESS_DENIED)
        }

        return step
    }
}