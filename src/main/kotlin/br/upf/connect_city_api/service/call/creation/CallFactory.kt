package br.upf.connect_city_api.service.call.creation

import br.upf.connect_city_api.dtos.call.CreateCallForCitizenRequestDTO
import br.upf.connect_city_api.dtos.call.CreateCallForMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.model.entity.address.Address
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Category
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.CategoryRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import br.upf.connect_city_api.util.constants.employee.MunicipalEmployeeMessages
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CallFactory(
    private val categoryRepository: CategoryRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository,
    private val callRepository: CallRepository,
    private val modelMapper: ModelMapper
) {
    fun createCallForCitizen(
        createRequest: CreateCallForCitizenRequestDTO,
        citizen: Citizen
    ): Call {
        val categories = getCategoriesFromRequest(createRequest.categoryIds)
        val address = modelMapper.map(createRequest.address, Address::class.java)
        val phoneNumber = createRequest.phoneNumber ?: citizen.phoneNumber
        val createdBy = "${citizen.firstName} ${citizen.lastName}"

        return Call(
            citizen = citizen,
            address = address,
            subject = createRequest.subject,
            description = createRequest.description,
            status = CallStatus.OPEN,
            priority = PriorityLevel.MEDIUM,
            createdBy = createdBy,
            phoneNumber = phoneNumber,
            isPublic = createRequest.isPublic ?: true,
            language = createRequest.language ?: "pt-BR",
            createdAt = LocalDateTime.now(),
            categories = categories
        )
    }

    fun createCallForEmployee(
        createRequest: CreateCallForMunicipalEmployeeRequestDTO,
        employee: MunicipalEmployee
    ): Call {
        val categories = getCategoriesFromRequest(createRequest.categoryIds)
        val address = modelMapper.map(createRequest.address, Address::class.java)
        val phoneNumber = createRequest.phoneNumber ?: employee.phoneNumber
        val createdBy = "${employee.firstName} ${employee.lastName}"

        return Call(
            employee = employee,
            address = address,
            subject = createRequest.subject,
            description = createRequest.description,
            status = CallStatus.OPEN,
            priority = PriorityLevel.MEDIUM,
            createdBy = createdBy,
            phoneNumber = phoneNumber,
            isPublic = createRequest.isPublic ?: true,
            language = createRequest.language ?: "pt-BR",
            createdAt = LocalDateTime.now(),
            categories = categories,
            tags = createRequest.tags?.toMutableList() ?: mutableListOf(),
            escalationLevel = createRequest.escalationLevel ?: 0,
            assignedTo = createRequest.assignedToEmployeeId?.let { getEmployeeById(it) },
            predecessorCalls = createRequest.predecessorCallIds?.let { callRepository.findAllById(it).toMutableList() }
                ?: mutableListOf(),
            successorCalls = createRequest.successorCallIds?.let { callRepository.findAllById(it).toMutableList() }
                ?: mutableListOf()
        )
    }

    private fun getCategoriesFromRequest(categoryIds: List<Long>?): MutableList<Category> {
        return categoryIds?.let { categoryRepository.findAllById(it).toMutableList() } ?: mutableListOf()
    }

    private fun getEmployeeById(employeeId: Long): MunicipalEmployee {
        return municipalEmployeeRepository.findById(employeeId)
            .orElseThrow { ResourceNotFoundError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND) }
    }
}