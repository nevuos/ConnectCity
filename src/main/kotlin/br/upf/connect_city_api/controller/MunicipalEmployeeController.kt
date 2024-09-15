package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.employee.CreateMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.dtos.employee.MunicipalEmployeeDetailsDTO
import br.upf.connect_city_api.dtos.employee.UpdateMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.user.MunicipalEmployeeService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/employee")
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
    @PutMapping
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
    @GetMapping("/search")
    fun search(
        @RequestParam(required = false) firstName: String?,
        @RequestParam(required = false) lastName: String?,
        @RequestParam(required = false) cpf: String?,
        @RequestParam(required = false) jobTitle: String?,
        @RequestParam(required = false) department: String?,
        pageable: Pageable
    ): ResponseEntity<Page<MunicipalEmployeeDetailsDTO>> {
        val employeeDetailsPage =
            municipalEmployeeService.search(firstName, lastName, cpf, jobTitle, department, pageable)
        return ResponseEntity.ok(employeeDetailsPage)
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @GetMapping("/me")
    fun getDetails(request: HttpServletRequest): ResponseEntity<MunicipalEmployeeDetailsDTO> {
        val employeeDetails = municipalEmployeeService.getDetails(request)
        return ResponseEntity.ok(employeeDetails)
    }
}