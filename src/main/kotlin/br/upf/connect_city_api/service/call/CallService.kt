package br.upf.connect_city_api.service.call

import CallMessages
import br.upf.connect_city_api.dtos.call.CallDTO
import br.upf.connect_city_api.dtos.call.CreateCallRequestDTO
import br.upf.connect_city_api.dtos.call.UpdateCallRequestDTO
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.CitizenRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CallService(
    private val callRepository: CallRepository,
    private val citizenRepository: CitizenRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository
) {

    fun create(request: HttpServletRequest, createRequest: CreateCallRequestDTO): String {
        val citizen = createRequest.citizenCpf?.let {
            citizenRepository.findByCpf(it) ?: throw RuntimeException(CallMessages.CITIZEN_NOT_FOUND)
        }

        val employee = createRequest.employeeId?.let {
            municipalEmployeeRepository.findById(it).orElseThrow { RuntimeException(CallMessages.EMPLOYEE_NOT_FOUND) }
        }

        val call = Call(
            citizen = citizen,
            employee = employee,
            address = createRequest.address,
            subject = createRequest.subject,
            description = createRequest.description,
            status = CallStatus.OPEN,
            priority = createRequest.priority,
            createdBy = request.userPrincipal.name,
            isPublic = true,
            language = "pt-BR",
            createdAt = LocalDateTime.now()
        )

        callRepository.save(call)
        return CallMessages.CALL_CREATED_SUCCESSFULLY
    }

    fun update(request: HttpServletRequest, id: Long, updateRequest: UpdateCallRequestDTO): String {
        val call = callRepository.findById(id)
            .orElseThrow { RuntimeException(CallMessages.CALL_NOT_FOUND) }

        updateRequest.subject?.let { call.subject = it }
        updateRequest.description?.let { call.description = it }
        updateRequest.priority?.let { call.priority = it }
        updateRequest.estimatedCompletion?.let { call.estimatedCompletion = it }
        updateRequest.progressPercentage?.let { call.progressPercentage = it }
        updateRequest.progressNotes?.let { call.progressNotes = it }
        updateRequest.isPublic?.let { call.isPublic = it }

        updateRequest.assignedToId?.let {
            val employee = municipalEmployeeRepository.findById(it)
                .orElseThrow { RuntimeException(CallMessages.EMPLOYEE_NOT_FOUND) }
            call.assignedTo = employee
        }

        call.updatedBy = request.userPrincipal.name
        callRepository.save(call)

        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }

    fun getById(request: HttpServletRequest, id: Long): CallDTO {
        val call = callRepository.findById(id)
            .orElseThrow { RuntimeException(CallMessages.CALL_NOT_FOUND) }
        return CallDTO(
            id = call.id,
            subject = call.subject,
            status = call.status,
            priority = call.priority,
            createdAt = call.createdAt,
            estimatedCompletion = call.estimatedCompletion
        )
    }

    fun delete(request: HttpServletRequest, id: Long): String {
        val call = callRepository.findById(id)
            .orElseThrow { RuntimeException(CallMessages.CALL_NOT_FOUND) }
        callRepository.delete(call)
        return CallMessages.CALL_DELETED_SUCCESSFULLY
    }

    fun search(request: HttpServletRequest, specs: Specification<Call>?): List<CallDTO> {
        val calls = callRepository.findAll(Specification.where(specs))
        return calls.map {
            CallDTO(
                id = it.id,
                subject = it.subject,
                status = it.status,
                priority = it.priority,
                createdAt = it.createdAt,
                estimatedCompletion = it.estimatedCompletion
            )
        }
    }
}