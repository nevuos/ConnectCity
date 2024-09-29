package br.upf.connect_city_api.service.call

import br.upf.connect_city_api.dtos.call.CreateCallRequestDTO
import br.upf.connect_city_api.dtos.call.CreatorUpdateCallRequestDTO
import br.upf.connect_city_api.dtos.call.ManagerUpdateCallRequestDTO
import br.upf.connect_city_api.model.entity.address.Address
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Category
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.CategoryRepository
import br.upf.connect_city_api.repository.CitizenRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.service.communication.NotificationService
import br.upf.connect_city_api.service.storage.AttachmentService
import br.upf.connect_city_api.util.constants.auth.AuthMessages
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import br.upf.connect_city_api.util.constants.user.UserMessages
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
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
    fun create(request: HttpServletRequest, createRequest: CreateCallRequestDTO, attachments: List<MultipartFile>?): String {
        val user = tokenService.getUserFromRequest(request)
        val (citizen, employee) = getUserProfile(user.id)
        val categories = getCategoriesFromRequest(createRequest.categoryIds)
        val address = modelMapper.map(createRequest.address, Address::class.java)
        val phoneNumber = createRequest.phoneNumber ?: citizen?.phoneNumber
        val createdBy = getProfileName(citizen, employee)
        val call = createCallEntity(createRequest, citizen, employee, categories, address, phoneNumber, createdBy)

        callRepository.save(call)
        attachmentService.processAttachments(call.attachments, attachments)
        notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_TYPE)

        employee?.let { timeLogService.logCallCompletion(call, it) }

        return CallMessages.CALL_CREATED_SUCCESSFULLY
    }

    @Transactional
    fun updateByCreator(
        request: HttpServletRequest,
        callId: Long,
        updateRequest: CreatorUpdateCallRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val (citizen, employee) = getUserProfile(user.id)
        val call =
            callRepository.findById(callId).orElseThrow { throw ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }
        if (call.citizen?.id != citizen?.id && call.employee?.id != employee?.id) throw PermissionDeniedError(
            AuthMessages.ACCESS_DENIED
        )

        modelMapper.map(updateRequest, call)
        attachmentService.processAttachments(call.attachments, attachments, removeAttachmentIds)

        if (updateRequest.status != null && updateRequest.status != call.status) {
            call.status = updateRequest.status
            notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_STATUS_CHANGE)
            employee?.let { timeLogService.logStepCompletion(call.steps.last(), it) }
        }

        call.updatedAt = LocalDateTime.now()
        callRepository.save(call)

        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }

    @Transactional
    fun updateByManager(
        request: HttpServletRequest,
        callId: Long,
        updateRequest: ManagerUpdateCallRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        val user = tokenService.getUserFromRequest(request)
        val (_, employee) = getUserProfile(user.id)
        val call =
            callRepository.findById(callId).orElseThrow { throw ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }
        if (call.assignedTo?.id != employee?.id) throw PermissionDeniedError(AuthMessages.ACCESS_DENIED)

        modelMapper.map(updateRequest, call)
        attachmentService.processAttachments(call.attachments, attachments, removeAttachmentIds)

        if (updateRequest.status != null && updateRequest.status != call.status) {
            call.status = updateRequest.status
            notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_STATUS_CHANGE)
            employee?.let { timeLogService.logStepCompletion(call.steps.last(), it) }
        }

        call.updatedAt = LocalDateTime.now()
        call.updatedBy = updateRequest.updatedBy ?: employee!!.firstName
        callRepository.save(call)

        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }

    internal fun getUserProfile(userId: Long): Pair<Citizen?, MunicipalEmployee?> {
        val citizen = citizenRepository.findById(userId).orElse(null)
        val employee = municipalEmployeeRepository.findById(userId).orElse(null)
        if (citizen == null && employee == null) throw ResourceNotFoundError(UserMessages.PROFILE_NOT_FOUND)
        return citizen to employee
    }

    private fun getCategoriesFromRequest(categoryIds: List<Long>?): MutableList<Category> {
        return categoryIds?.let { categoryRepository.findAllById(it).toMutableList() } ?: mutableListOf()
    }

    private fun getProfileName(citizen: Citizen?, employee: MunicipalEmployee?): String {
        return when {
            citizen != null -> "${citizen.firstName} ${citizen.lastName}"
            employee != null -> "${employee.firstName} ${employee.lastName}"
            else -> throw ResourceNotFoundError(UserMessages.PROFILE_NOT_FOUND)
        }
    }

    private fun createCallEntity(
        createRequest: CreateCallRequestDTO,
        citizen: Citizen?,
        employee: MunicipalEmployee?,
        categories: MutableList<Category>,
        address: Address,
        phoneNumber: String?,
        createdBy: String
    ): Call {
        return Call(
            citizen = citizen,
            employee = employee,
            address = address,
            subject = createRequest.subject,
            description = createRequest.description,
            status = CallStatus.OPEN,
            priority = createRequest.priority,
            createdBy = createdBy,
            phoneNumber = phoneNumber ?: "",
            isPublic = createRequest.isPublic ?: true,
            language = createRequest.language ?: "pt-BR",
            createdAt = LocalDateTime.now(),
            categories = categories
        )
    }
}