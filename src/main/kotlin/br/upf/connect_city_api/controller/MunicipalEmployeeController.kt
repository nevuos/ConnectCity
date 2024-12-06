package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.employee.CreateMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.dtos.employee.MunicipalEmployeeDetailsDTO
import br.upf.connect_city_api.dtos.employee.UpdateMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.model.entity.enums.EmployeeType
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.user.MunicipalEmployeeService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/employees")
class MunicipalEmployeeController(
    private val municipalEmployeeService: MunicipalEmployeeService,
) {

    @UserTypeRequired(UserType.TEMPORARY)
    @PostMapping
    fun create(
        @Valid @RequestBody createRequest: CreateMunicipalEmployeeRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = municipalEmployeeService.create(request, createRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @GetMapping("/me")
    fun getDetails(request: HttpServletRequest): ResponseEntity<MunicipalEmployeeDetailsDTO> {
        val employeeDetails = municipalEmployeeService.getDetails(request)
        return ResponseEntity.ok(employeeDetails)
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @PatchMapping
    fun update(
        @Valid @RequestBody updateRequest: UpdateMunicipalEmployeeRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = municipalEmployeeService.update(request, updateRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @PutMapping("/{id}/approve")
    fun approve(
        @PathVariable id: Long,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = municipalEmployeeService.approve(id, request)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @PatchMapping("/{employeeId}/manager-status")
    fun updateManagerStatus(
        @PathVariable employeeId: Long,
        @RequestParam isManager: Boolean
    ): ResponseEntity<ApiResponseDTO> {
        val message = municipalEmployeeService.updateManagerStatus(employeeId, isManager)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN, UserType.MUNICIPAL_EMPLOYEE)
    @GetMapping("/search/{id}")
    fun getById(
        @PathVariable id: Long
    ): ResponseEntity<MunicipalEmployeeDetailsDTO> {
        val employeeDetails = municipalEmployeeService.getById(id)
        return ResponseEntity.ok(employeeDetails)
    }

    @UserTypeRequired(UserType.ADMIN, UserType.MUNICIPAL_EMPLOYEE)
    @GetMapping("/search")
    fun search(
        @RequestParam(required = false) firstName: String?,
        @RequestParam(required = false) lastName: String?,
        @RequestParam(required = false) cpf: String?,
        @RequestParam(required = false) jobTitle: String?,
        @RequestParam(required = false) department: String?,
        @RequestParam(required = false) isApproved: Boolean?,
        @RequestParam(required = false) isManager: Boolean?,
        @RequestParam(required = false) dateOfBirth: LocalDate?,
        @RequestParam(required = false) gender: String?,
        @RequestParam(required = false) phoneNumber: String?,
        @RequestParam(required = false) employeeType: EmployeeType?,
        pageable: Pageable
    ): ResponseEntity<Page<MunicipalEmployeeDetailsDTO>> {
        val employeeDetailsPage = municipalEmployeeService.search(
            firstName, lastName, cpf, jobTitle, department, isApproved, isManager, dateOfBirth, gender, phoneNumber, employeeType, pageable
        )
        return ResponseEntity.ok(employeeDetailsPage)
    }
}