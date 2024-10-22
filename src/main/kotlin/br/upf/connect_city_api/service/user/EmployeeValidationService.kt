package br.upf.connect_city_api.service.user

import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.util.constants.employee.MunicipalEmployeeMessages
import br.upf.connect_city_api.util.exception.PermissionDeniedError

import org.springframework.stereotype.Service

@Service
class EmployeeValidationService {

    fun checkIfEmployeeIsApproved(employee: MunicipalEmployee) {
        if (!employee.isApproved) {
            throw PermissionDeniedError(MunicipalEmployeeMessages.EMPLOYEE_NOT_APPROVED)
        }
    }

    fun checkIfEmployeeIsManager(employee: MunicipalEmployee) {
        if (!employee.isManager) {
            throw PermissionDeniedError(MunicipalEmployeeMessages.EMPLOYEE_NOT_MANAGER)
        }
    }
}