package br.upf.connect_city_api.service.call.creation

import br.upf.connect_city_api.dtos.call.CreateCallForCitizenRequestDTO
import br.upf.connect_city_api.dtos.call.CreateCallForMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.service.communication.NotificationService
import br.upf.connect_city_api.service.storage.AttachmentService
import br.upf.connect_city_api.service.user.EmployeeValidationService
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class CallCreationService(
    private val callFactory: CallFactory,
    private val callRepository: CallRepository,
    private val attachmentService: AttachmentService,
    private val notificationService: NotificationService,
    private val employeeValidationService: EmployeeValidationService
) {
    @Transactional
    fun createCallForCitizen(
        user: User,
        createRequest: CreateCallForCitizenRequestDTO,
        attachments: List<MultipartFile>?,
        citizen: br.upf.connect_city_api.model.entity.user.Citizen
    ): String {
        val call = callFactory.createCallForCitizen(createRequest, citizen)
        callRepository.save(call)
        attachmentService.processAttachmentsForCall(call.id!!, attachments)

        val customMessage = NotificationMessages.CALL_CREATED_MESSAGE_CITIZEN.replace("{CALL_ID}", call.id.toString())
        notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_TYPE_CALL, user.email, customMessage)
        return CallMessages.CALL_CREATED_SUCCESSFULLY
    }

    @Transactional
    fun createCallForMunicipalEmployee(
        user: User,
        createRequest: CreateCallForMunicipalEmployeeRequestDTO,
        attachments: List<MultipartFile>?,
        employee: br.upf.connect_city_api.model.entity.user.MunicipalEmployee
    ): String {
        employeeValidationService.checkIfEmployeeIsApproved(employee)

        val call = callFactory.createCallForEmployee(createRequest, employee)
        callRepository.save(call)
        attachmentService.processAttachmentsForCall(call.id!!, attachments)

        val customMessage = NotificationMessages.CALL_CREATED_MESSAGE_EMPLOYEE.replace("{CALL_ID}", call.id.toString())
        notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_TYPE_CALL, user.email, customMessage)
        return CallMessages.CALL_CREATED_SUCCESSFULLY
    }
}