package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.category.CategoryDetailsDTO
import br.upf.connect_city_api.dtos.category.CreateCategoryRequestDTO
import br.upf.connect_city_api.dtos.category.UpdateCategoryRequestDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.call.category.CategoryService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @UserTypeRequired(UserType.ADMIN)
    @PostMapping
    fun createCategory(
        request: HttpServletRequest, @RequestBody @Valid createRequest: CreateCategoryRequestDTO
    ): ResponseEntity<ApiResponseDTO> {
        val message = categoryService.createCategory(request, createRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @PatchMapping("/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: Long,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestBody(required = false) @Valid updateRequest: UpdateCategoryRequestDTO?
    ): ResponseEntity<ApiResponseDTO> {
        val message = categoryService.updateCategory(categoryId, updateRequest, isActive)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN, UserType.MUNICIPAL_EMPLOYEE, UserType.ADMIN)
    @GetMapping("/search")
    fun searchCategories(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) parentCategoryId: Long?,
        @RequestParam(required = false) createdAfter: LocalDateTime?,
        @RequestParam(required = false) createdBefore: LocalDateTime?,
        @RequestParam(required = false) createdOn: LocalDate?,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestParam(required = false) createdBy: String?,
        pageable: Pageable
    ): ResponseEntity<Page<CategoryDetailsDTO>> {
        val categories = categoryService.searchCategories(
            name = name,
            parentCategoryId = parentCategoryId,
            createdAfter = createdAfter,
            createdBefore = createdBefore,
            createdOn = createdOn,
            isActive = isActive,
            createdBy = createdBy,
            pageable = pageable
        )
        return ResponseEntity.ok(categories)
    }
}