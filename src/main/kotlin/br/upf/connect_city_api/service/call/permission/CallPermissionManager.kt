package br.upf.connect_city_api.service.call.permission

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.repository.CitizenRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import br.upf.connect_city_api.service.user.EmployeeValidationService
import br.upf.connect_city_api.util.constants.call.PermissionMessages
import br.upf.connect_city_api.util.constants.citizen.CitizenMessages
import br.upf.connect_city_api.util.constants.employee.MunicipalEmployeeMessages
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import org.springframework.stereotype.Service

@Service
class CallPermissionManager(
    private val citizenRepository: CitizenRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository,
    private val employeeValidationService: EmployeeValidationService
) {

    fun getCitizenByUserId(userId: Long): Citizen {
        return citizenRepository.findByUserId(userId)
            ?: throw PermissionDeniedError(CitizenMessages.CITIZEN_NOT_FOUND)
    }

    fun getEmployeeByUserId(userId: Long): MunicipalEmployee {
        return municipalEmployeeRepository.findByUserId(userId)
            ?: throw PermissionDeniedError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND)
    }

    fun checkCitizenPermission(citizenId: Long?, callCitizenId: Long?) {
        if (citizenId == null) {
            throw PermissionDeniedError(PermissionMessages.CITIZEN_NOT_IDENTIFIED)
        }
        if (callCitizenId == null) {
            throw PermissionDeniedError(PermissionMessages.CALL_WITHOUT_CITIZEN)
        }
        if (citizenId != callCitizenId) {
            throw PermissionDeniedError(
                PermissionMessages.CITIZEN_NO_PERMISSION
                    .replace("{CITIZEN_ID}", citizenId.toString())
                    .replace("{CALL_CITIZEN_ID}", callCitizenId.toString())
            )
        }
    }

    fun checkEmployeePermission(employeeId: Long?, callEmployeeId: Long?) {
        if (employeeId == null) {
            throw PermissionDeniedError(PermissionMessages.EMPLOYEE_NOT_IDENTIFIED)
        }

        val employee = municipalEmployeeRepository.findById(employeeId)
            .orElseThrow { PermissionDeniedError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND) }

        employeeValidationService.checkIfEmployeeIsApproved(employee)

        if (callEmployeeId == null || employeeId != callEmployeeId) {
            throw PermissionDeniedError(
                PermissionMessages.EMPLOYEE_NO_PERMISSION
                    .replace("{EMPLOYEE_ID}", employeeId.toString())
                    .replace("{CALL_EMPLOYEE_ID}", callEmployeeId.toString())
            )
        }
    }

    fun checkManagerPermission(employeeId: Long?, assignedEmployeeId: Long?) {
        if (employeeId == null) {
            throw PermissionDeniedError(PermissionMessages.MANAGER_NOT_IDENTIFIED)
        }

        val employee = municipalEmployeeRepository.findById(employeeId)
            .orElseThrow { PermissionDeniedError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND) }

        employeeValidationService.checkIfEmployeeIsApproved(employee)
        employeeValidationService.checkIfEmployeeIsManager(employee)

        if (assignedEmployeeId == null || employeeId != assignedEmployeeId) {
            throw PermissionDeniedError(
                PermissionMessages.MANAGER_NO_PERMISSION
                    .replace("{EMPLOYEE_ID}", employeeId.toString())
                    .replace("{ASSIGNED_EMPLOYEE_ID}", assignedEmployeeId.toString())
            )
        }
    }

    fun checkUpdatePermission(call: Call) {
        if (call.assignedTo != null) {
            throw PermissionDeniedError(
                PermissionMessages.CALL_ASSIGNED_TO_EMPLOYEE
                    .replace("{ASSIGNED_TO_ID}", call.assignedTo?.id.toString())
            )
        }
    }
}