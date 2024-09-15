package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.citizen.CitizenDetailsDTO
import br.upf.connect_city_api.dtos.citizen.CreateCitizenRequestDTO
import br.upf.connect_city_api.dtos.citizen.UpdateCitizenRequestDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.user.CitizenService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/citizen")
class CitizenController(
    private val citizenService: CitizenService,
) {

    @UserTypeRequired(UserType.TEMPORARY)
    @PostMapping
    fun create(
        @Valid @RequestBody createRequest: CreateCitizenRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = citizenService.create(request, createRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN)
    @PatchMapping
    fun update(
        @Valid @RequestBody updateRequest: UpdateCitizenRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = citizenService.update(request, updateRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN)
    @GetMapping("/me")
    fun getDetails(request: HttpServletRequest): ResponseEntity<CitizenDetailsDTO> {
        val citizenDetails = citizenService.getDetails(request)
        return ResponseEntity.ok(citizenDetails)
    }

    @UserTypeRequired(UserType.ADMIN, UserType.MUNICIPAL_EMPLOYEE)
    @GetMapping("/search")
    fun search(
        @RequestParam(required = false) firstName: String?,
        @RequestParam(required = false) lastName: String?,
        @RequestParam(required = false) cpf: String?,
        @RequestParam(required = false) dateOfBirth: LocalDate?,
        @RequestParam(required = false) gender: String?,
        @RequestParam(required = false) phoneNumber: String?,
        pageable: Pageable
    ): ResponseEntity<Page<CitizenDetailsDTO>> {
        val citizenDetailsPage =
            citizenService.search(firstName, lastName, cpf, dateOfBirth, gender, phoneNumber, pageable)
        return ResponseEntity.ok(citizenDetailsPage)
    }
}