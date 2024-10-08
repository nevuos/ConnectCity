package br.upf.connect_city_api.service.call

import br.upf.connect_city_api.dtos.call.*
import br.upf.connect_city_api.model.entity.address.Address
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Category
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.repository.*
import br.upf.connect_city_api.repository.specifications.CallSpecifications
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.service.communication.NotificationService
import br.upf.connect_city_api.service.storage.AttachmentService
import br.upf.connect_city_api.util.constants.auth.AuthMessages
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.constants.citizen.CitizenMessages
import br.upf.connect_city_api.util.constants.employee.MunicipalEmployeeMessages
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import br.upf.connect_city_api.util.constants.user.UserMessages
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class CallService(
    private val callRepository: CallRepository,
    private val citizenRepository: CitizenRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository,
    private val categoryRepository: CategoryRepository,
    private val tokenService: TokenService,
    private val notificationService: NotificationService,
    private val timeLogService: TimeLogService,
    private val modelMapper: ModelMapper,
    private val attachmentService: AttachmentService
) {

    @Transactional
    @CacheEvict(value = ["callById", "searchCalls"], allEntries = true, cacheManager = "searchCacheManager")
    fun createCallForCitizen(
        request: HttpServletRequest,
        createRequest: CreateCallForCitizenRequestDTO,
        attachments: List<MultipartFile>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val citizen = citizenRepository.findByUserId(user.id)
            ?: throw PermissionDeniedError(CitizenMessages.CITIZEN_NOT_FOUND)

        val call = createCallEntityForCitizen(createRequest, citizen)
        processAttachmentsAndSaveCall(call, attachments, user.email)

        return CallMessages.CALL_CREATED_SUCCESSFULLY
    }

    @Transactional
    @CacheEvict(value = ["callById", "searchCalls"], allEntries = true, cacheManager = "searchCacheManager")
    fun createCallForMunicipalEmployee(
        request: HttpServletRequest,
        createRequest: CreateCallForMunicipalEmployeeRequestDTO,
        attachments: List<MultipartFile>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val employee = municipalEmployeeRepository.findByUserId(user.id)
            ?: throw PermissionDeniedError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND)

        val call = createCallEntityForEmployee(createRequest, employee)
        processAttachmentsAndSaveCall(call, attachments, user.email)

        return CallMessages.CALL_CREATED_SUCCESSFULLY
    }

    @Transactional
    @CacheEvict(value = ["callById", "searchCalls"], key = "#callId", allEntries = true, cacheManager = "searchCacheManager")
    fun updateByCreator(
        request: HttpServletRequest,
        callId: Long,
        updateRequest: CreatorUpdateCallRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val call = findCallByUser(callId, user.id)

        modelMapper.map(updateRequest, call)
        processAttachmentsForUpdate(callId, attachments, removeAttachmentIds)
        handleStatusChange(call, updateRequest.status, user.email)

        call.updatedAt = LocalDateTime.now()
        callRepository.save(call)

        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }

    @Transactional
    @CacheEvict(value = ["callById", "searchCalls"], key = "#callId", allEntries = true, cacheManager = "searchCacheManager")
    fun updateByManager(
        request: HttpServletRequest,
        callId: Long,
        updateRequest: ManagerUpdateCallRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val call = findAssignedCall(callId, user.id)

        modelMapper.map(updateRequest, call)
        processAttachmentsForUpdate(callId, attachments, removeAttachmentIds)
        handleStatusChange(call, updateRequest.status, user.email)

        call.updatedAt = LocalDateTime.now()
        call.updatedBy = updateRequest.updatedBy ?: "${call.assignedTo?.firstName} ${call.assignedTo?.lastName}"
        callRepository.save(call)

        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }

    @Transactional
    @CacheEvict(value = ["callById", "searchCalls"], key = "#callId", allEntries = true, cacheManager = "searchCacheManager")
    fun assignCallToEmployee(request: HttpServletRequest, callId: Long, employeeId: Long): String {
        val user = tokenService.getUserFromRequest(request)
        val call = findAssignedCall(callId, user.id)

        val employeeToAssign = municipalEmployeeRepository.findById(employeeId)
            .orElseThrow { ResourceNotFoundError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND) }

        call.assignedTo = employeeToAssign
        call.status = CallStatus.IN_PROGRESS
        call.updatedAt = LocalDateTime.now()
        call.updatedBy = "${call.assignedTo?.firstName} ${call.assignedTo?.lastName}"
        callRepository.save(call)

        notificationService.sendNotification(call, NotificationMessages.CALL_ASSIGNED, user.email)
        return CallMessages.CALL_ASSIGNED_SUCCESSFULLY
    }

    @Cacheable(value = ["callById"], key = "#callId", cacheManager = "searchCacheManager")
    fun getCallById(callId: Long): Call {
        return callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }
    }

    @Cacheable(value = ["searchCalls"], key = "{#subject, #description, #statuses, #priorities, #citizenName, #employeeName, #categoryIds, #createdAtStart, #createdAtEnd, #closedAtStart, #closedAtEnd, #estimatedCompletionStart, #estimatedCompletionEnd}", cacheManager = "searchCacheManager")
    fun search(
        subject: String?,
        description: String?,
        statuses: List<CallStatus>?,
        priorities: List<PriorityLevel>?,
        citizenName: String?,
        employeeName: String?,
        categoryIds: List<Long>?,
        createdAtStart: LocalDateTime?,
        createdAtEnd: LocalDateTime?,
        closedAtStart: LocalDateTime?,
        closedAtEnd: LocalDateTime?,
        estimatedCompletionStart: LocalDateTime?,
        estimatedCompletionEnd: LocalDateTime?
    ): List<CallDetailsDTO> {
        val specifications = listOfNotNull(
            CallSpecifications.hasSubject(subject),
            CallSpecifications.hasDescription(description),
            CallSpecifications.hasStatuses(statuses),
            CallSpecifications.hasPriorities(priorities),
            CallSpecifications.citizenNameContains(citizenName),
            CallSpecifications.employeeNameContains(employeeName),
            CallSpecifications.hasCategories(categoryIds),
            CallSpecifications.createdAtBetween(createdAtStart, createdAtEnd),
            CallSpecifications.closedAtBetween(closedAtStart, closedAtEnd),
            CallSpecifications.estimatedCompletionBetween(estimatedCompletionStart, estimatedCompletionEnd)
        ).reduceOrNull { spec1, spec2 -> spec1.and(spec2) } ?: Specification.where(null)

        return callRepository.findAll(specifications).map { call ->
            modelMapper.map(call, CallDetailsDTO::class.java)
        }
    }

    private fun createCallEntityForCitizen(
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

    private fun createCallEntityForEmployee(
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
            priority = createRequest.priority,
            createdBy = createdBy,
            phoneNumber = phoneNumber,
            isPublic = createRequest.isPublic ?: true,
            language = createRequest.language ?: "pt-BR",
            createdAt = LocalDateTime.now(),
            categories = categories,
            tags = createRequest.tags?.toMutableList() ?: mutableListOf(),
            escalationLevel = createRequest.escalationLevel ?: 0,
            assignedTo = createRequest.assignedToEmployeeId?.let { municipalEmployeeRepository.findById(it).orElse(null) },
            predecessorCalls = createRequest.predecessorCallIds?.let { callRepository.findAllById(it).toMutableList() } ?: mutableListOf(),
            successorCalls = createRequest.successorCallIds?.let { callRepository.findAllById(it).toMutableList() } ?: mutableListOf()
        )
    }

    private fun processAttachmentsAndSaveCall(call: Call, attachments: List<MultipartFile>?, userEmail: String) {
        callRepository.save(call)
        attachmentService.processAttachmentsForCall(call.id!!, attachments)
        notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_TYPE_CALL, userEmail)
    }


    private fun processAttachmentsForUpdate(callId: Long, attachments: List<MultipartFile>?, removeAttachmentIds: List<Long>?) {
        attachmentService.processAttachmentsForCall(callId, attachments, removeAttachmentIds)
    }

    private fun handleStatusChange(call: Call, newStatus: CallStatus?, userEmail: String?) {
        if (newStatus != null && newStatus != call.status) {
            call.status = newStatus
            notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_STATUS_CHANGE, userEmail)
            call.assignedTo?.let { timeLogService.logStepCompletion(call.steps.last(), it) }
        }
    }

    private fun findCallByUser(callId: Long, userId: Long): Call {
        val (citizen, employee) = getUserProfile(userId)
        return callRepository.findById(callId).orElseThrow { ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }
            .apply {
                if (citizen?.id != this.citizen?.id && employee?.id != this.employee?.id) {
                    throw PermissionDeniedError(AuthMessages.ACCESS_DENIED)
                }
            }
    }

    private fun findAssignedCall(callId: Long, userId: Long): Call {
        val (_, employee) = getUserProfile(userId)
        return callRepository.findById(callId).orElseThrow { ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }
            .apply {
                if (this.assignedTo?.id != employee?.id) {
                    throw PermissionDeniedError(AuthMessages.ACCESS_DENIED)
                }
            }
    }

    internal fun getUserProfile(userId: Long): Pair<Citizen?, MunicipalEmployee?> {
        val citizen = citizenRepository.findByUserId(userId)
        val employee = municipalEmployeeRepository.findByUserId(userId)
        if (citizen == null && employee == null) throw ResourceNotFoundError(UserMessages.PROFILE_NOT_FOUND)
        return citizen to employee
    }

    private fun getCategoriesFromRequest(categoryIds: List<Long>?): MutableList<Category> {
        return categoryIds?.let { categoryRepository.findAllById(it).toMutableList() } ?: mutableListOf()
    }
}