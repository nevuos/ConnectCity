package br.upf.connect_city_api.service.call

import br.upf.connect_city_api.dtos.call.*
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.specifications.CallSpecifications
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.service.call.assignment.CallAssignmentService
import br.upf.connect_city_api.service.call.creation.CallCreationService
import br.upf.connect_city_api.service.call.permission.CallPermissionManager
import br.upf.connect_city_api.service.call.update.CallUpdateService
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class CallService(
    private val callRepository: CallRepository,
    private val tokenService: TokenService,
    private val callPermissionManager: CallPermissionManager,
    private val callCreationService: CallCreationService,
    private val callUpdateService: CallUpdateService,
    private val callAssignmentService: CallAssignmentService,
    private val modelMapper: ModelMapper
) {

    @Transactional
    @CacheEvict(value = ["callById", "callDetailsById", "searchCalls"], allEntries = true, cacheManager = "searchCacheManager")
    fun createCallForCitizen(
        request: HttpServletRequest,
        createRequest: CreateCallForCitizenRequestDTO,
        attachments: List<MultipartFile>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val citizen = callPermissionManager.getCitizenByUserId(user.id)
        return callCreationService.createCallForCitizen(user, createRequest, attachments, citizen)
    }

    @Transactional
    @CacheEvict(value = ["callById", "callDetailsById", "searchCalls"], allEntries = true, cacheManager = "searchCacheManager")
    fun createCallForMunicipalEmployee(
        request: HttpServletRequest,
        createRequest: CreateCallForMunicipalEmployeeRequestDTO,
        attachments: List<MultipartFile>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val employee = callPermissionManager.getEmployeeByUserId(user.id)
        return callCreationService.createCallForMunicipalEmployee(user, createRequest, attachments, employee)
    }

    @Transactional
    @CacheEvict(value = ["callById", "callDetailsById", "searchCalls"], key = "#callId", allEntries = true, cacheManager = "searchCacheManager")
    fun updateByCitizenCreator(
        request: HttpServletRequest,
        callId: Long,
        updateRequest: UpdateCallByCitizenCreatorRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val call = getCallById(callId)

        callPermissionManager.checkCitizenPermission(user.id, call.citizen?.id)
        callPermissionManager.checkUpdatePermission(call)

        return callUpdateService.updateByCitizenCreator(user, call, updateRequest, attachments, removeAttachmentIds)
    }

    @Transactional
    @CacheEvict(value = ["callById", "callDetailsById", "searchCalls"], key = "#callId", allEntries = true, cacheManager = "searchCacheManager")
    fun updateByMunicipalEmployeeCreator(
        request: HttpServletRequest,
        callId: Long,
        updateRequest: UpdateCallByMunicipalEmployeeCreatorRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val call = getCallById(callId)

        callPermissionManager.checkEmployeePermission(user.id, call.employee?.id)
        callPermissionManager.checkUpdatePermission(call)

        return callUpdateService.updateByMunicipalEmployeeCreator(
            user,
            call,
            updateRequest,
            attachments,
            removeAttachmentIds
        )
    }

    @Transactional
    @CacheEvict(
        value = ["callById", "callDetailsById", "searchCalls"],
        key = "#callId",
        allEntries = true,
        cacheManager = "searchCacheManager"
    )
    fun updateByManager(
        request: HttpServletRequest,
        callId: Long,
        updateRequest: UpdateCallByManagerRequestDTO,
        attachments: List<MultipartFile>? = null,
        removeAttachmentIds: List<Long>? = null
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val call = getCallById(callId)

        val employee = callPermissionManager.getEmployeeByUserId(user.id)
        callPermissionManager.checkManagerPermission(employee.id, call.assignedTo?.id)

        return callUpdateService.updateByManager(user, call, updateRequest, attachments, removeAttachmentIds)
    }

    @Transactional
    @CacheEvict(value = ["callById", "callDetailsById", "searchCalls"], key = "#callId", allEntries = true, cacheManager = "searchCacheManager")
    fun assignCallToEmployee(request: HttpServletRequest, callId: Long, employeeId: Long): String {
        val user = tokenService.getUserFromRequest(request)
        return callAssignmentService.assignCallToEmployee(user, callId, employeeId)
    }

    @Cacheable(value = ["callById"], key = "#callId", cacheManager = "searchCacheManager")
    fun getCallById(callId: Long): Call {
        return callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }
    }

    @Cacheable(value = ["callDetailsById"], key = "#callId", cacheManager = "searchCacheManager")
    @Transactional(readOnly = true)
    fun getCallDetailsById(callId: Long): CallDetailsDTO {
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }
        val callDTO = modelMapper.map(call, CallDetailsDTO::class.java)
        callDTO.notificationHistory = call.notificationHistory.map { notification ->
            modelMapper.map(notification, NotificationDTO::class.java)
        }

        return callDTO
    }

    @Transactional(readOnly = true)
    @Cacheable(
        value = ["searchCalls"],
        key = "{#subject, #description, #status, #priority, #citizenName, #employeeName, #createdBy, #isPublic, #categoryIds, #createdAtStart, #createdAtEnd, #closedAtStart, #closedAtEnd, #estimatedCompletionStart, #estimatedCompletionEnd, #citizenId, #employeeId, #pageable}",
        cacheManager = "searchCacheManager"
    )
    fun search(
        subject: String? = null,
        description: String? = null,
        status: List<CallStatus>? = null,
        priority: List<PriorityLevel>? = null,
        citizenName: String? = null,
        employeeName: String? = null,
        createdBy: String? = null,
        isPublic: Boolean? = null,
        categoryIds: List<Long>? = null,
        createdAtStart: LocalDateTime? = null,
        createdAtEnd: LocalDateTime? = null,
        closedAtStart: LocalDateTime? = null,
        closedAtEnd: LocalDateTime? = null,
        estimatedCompletionStart: LocalDateTime? = null,
        estimatedCompletionEnd: LocalDateTime? = null,
        citizenId: Long? = null, // Novo filtro
        employeeId: Long? = null, // Novo filtro
        pageable: Pageable
    ): Page<CallDetailsDTO> {
        val spec = Specification.where(subject?.let { CallSpecifications.hasSubject(it) })
            .and(description?.let { CallSpecifications.hasDescription(it) })
            .and(status?.let { CallSpecifications.hasStatus(it) })
            .and(priority?.let { CallSpecifications.hasPriority(it) })
            .and(citizenName?.let { CallSpecifications.citizenNameContains(it) })
            .and(employeeName?.let { CallSpecifications.employeeNameContains(it) })
            .and(createdBy?.let { CallSpecifications.hasCreatedBy(it) })
            .and(isPublic?.let { CallSpecifications.hasIsPublic(it) })
            .and(categoryIds?.let { CallSpecifications.hasCategories(it) })
            .and(createdAtStart?.let { CallSpecifications.createdAtBetween(it, createdAtEnd) })
            .and(closedAtStart?.let { CallSpecifications.closedAtBetween(it, closedAtEnd) })
            .and(estimatedCompletionStart?.let { CallSpecifications.estimatedCompletionBetween(it, estimatedCompletionEnd) })
            .and(citizenId?.let { CallSpecifications.hasCitizenId(it) })
            .and(employeeId?.let { CallSpecifications.hasEmployeeId(it) })

        val calls = callRepository.findAll(spec, pageable)
        return calls.map { call ->
            val callDTO = modelMapper.map(call, CallDetailsDTO::class.java)
            callDTO.notificationHistory = call.notificationHistory.map { notification ->
                modelMapper.map(notification, NotificationDTO::class.java)
            }
            callDTO
        }
    }
}