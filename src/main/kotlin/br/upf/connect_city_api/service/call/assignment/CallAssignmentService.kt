package br.upf.connect_city_api.service.call.assignment

import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import br.upf.connect_city_api.service.communication.NotificationService
import br.upf.connect_city_api.util.constants.auth.AuthMessages
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.constants.employee.MunicipalEmployeeMessages
import br.upf.connect_city_api.util.constants.notification.NotificationMessages
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CallAssignmentService(
    private val callRepository: CallRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository,
    private val notificationService: NotificationService
) {

    @Transactional
    fun assignCallToEmployee(user: User, callId: Long, employeeId: Long): String {
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }

        val currentEmployee = municipalEmployeeRepository.findByUserId(user.id)
            ?: throw PermissionDeniedError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND)

        if (!currentEmployee.isManager && call.employee?.id != currentEmployee.id) {
            throw PermissionDeniedError(AuthMessages.ACCESS_DENIED)
        }

        val employeeToAssign = municipalEmployeeRepository.findById(employeeId)
            .orElseThrow { ResourceNotFoundError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND) }

        if (call.assignedTo == null) {
            if (!employeeToAssign.isManager) {
                throw PermissionDeniedError(MunicipalEmployeeMessages.ONLY_MANAGER_CAN_BE_ASSIGNED_FIRST)
            }
        } else {
            if (!currentEmployee.isManager) {
                throw PermissionDeniedError(CallMessages.ONLY_MANAGER_CAN_ASSIGN)
            }
        }

        call.assignedTo = employeeToAssign
        call.status = CallStatus.IN_PROGRESS
        call.updatedAt = LocalDateTime.now()
        call.updatedBy = "${employeeToAssign.firstName} ${employeeToAssign.lastName}"
        callRepository.save(call)

        val customMessage = NotificationMessages.CALL_ASSIGNED_MESSAGE.replace("{EMPLOYEE_NAME}", "${employeeToAssign.firstName} ${employeeToAssign.lastName}")
        notificationService.sendNotification(call, NotificationMessages.NOTIFICATION_TYPE_CALL, user.email, customMessage)
        return CallMessages.CALL_ASSIGNED_SUCCESSFULLY
    }
}