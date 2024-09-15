package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.user.UpdateUserRequestDTO
import br.upf.connect_city_api.dtos.user.UserDetailsDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.user.UserManagementService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/users")
class UserController(
    private val userManagementService: UserManagementService
) {

    @GetMapping("/me")
    fun getDetails(request: HttpServletRequest): ResponseEntity<UserDetailsDTO> {
        val userDetails = userManagementService.getDetails(request)
        return ResponseEntity.ok(userDetails)
    }

    @PatchMapping
    fun update(
        @Valid @RequestBody updateRequest: UpdateUserRequestDTO,
        @RequestParam("isActive", required = false) isActive: Boolean?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponseDTO> {
        val message = userManagementService.update(
            request = request,
            updateRequest = updateRequest,
            isActive = isActive,
            response = response
        )
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @PutMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestParam("isActive") isActive: Boolean,
    ): ResponseEntity<ApiResponseDTO> {
        val message = userManagementService.updateStatusByAdmin(id, isActive = isActive)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @GetMapping("/search")
    fun search(
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestParam(required = false) createdAfter: LocalDateTime?,
        @RequestParam(required = false) createdBefore: LocalDateTime?,
        @RequestParam(required = false) createdOn: LocalDateTime?,
        pageable: Pageable
    ): ResponseEntity<Page<UserDetailsDTO>> {
        val userDetailsPage =
            userManagementService.search(
                username,
                email,
                type,
                isActive,
                createdAfter,
                createdBefore,
                createdOn,
                pageable
            )
        return ResponseEntity.ok(userDetailsPage)
    }
}