package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.call.CreateCallRequestDTO
import br.upf.connect_city_api.dtos.call.CreateCategoryRequestDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.call.CallService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/call")
class CallController(
    private val callService: CallService,
) {

    @UserTypeRequired(UserType.CITIZEN)
    @PostMapping(consumes = ["multipart/form-data"])
    fun create(
        @Valid @RequestPart("callData") createRequest: CreateCallRequestDTO,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.create(request, createRequest, attachments)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @PostMapping("/categories")
    fun createCategory(
        @RequestBody @Valid createRequest: CreateCategoryRequestDTO
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.createCategory(createRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }
}