package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.call.*
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.call.CallService
import br.upf.connect_city_api.service.call.CategoryService
import br.upf.connect_city_api.service.call.InteractionService
import br.upf.connect_city_api.service.call.StepService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/calls")
class CallController(
    private val callService: CallService,
    private val stepService: StepService,
    private val interactionService: InteractionService,
    private val categoryService: CategoryService
) {

    @UserTypeRequired(UserType.CITIZEN)
    @PostMapping("/citizen")
    fun createCallForCitizen(
        @Valid @RequestPart("callData") createRequest: CreateCallForCitizenRequestDTO,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.createCallForCitizen(request, createRequest, attachments)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @PostMapping("/municipal-employee")
    fun createCallForMunicipalEmployee(
        @Valid @RequestPart("callData") createRequest: CreateCallForMunicipalEmployeeRequestDTO,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.createCallForMunicipalEmployee(request, createRequest, attachments)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @PostMapping("/categories")
    fun createCategory(
        @RequestBody @Valid createRequest: CreateCategoryRequestDTO
    ): ResponseEntity<ApiResponseDTO> {
        val message = categoryService.createCategory(createRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN, UserType.MUNICIPAL_EMPLOYEE)
    @PatchMapping("/{callId}/update")
    fun updateCallByCreator(
        @PathVariable callId: Long,
        @RequestPart("updateData") @Valid updateRequest: CreatorUpdateCallRequestDTO,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?,
        @RequestParam("removeAttachmentIds", required = false) removeAttachmentIds: List<Long>?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.updateByCreator(request, callId, updateRequest, attachments, removeAttachmentIds)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @PatchMapping("/{callId}/manage")
    fun updateCallByManager(
        @PathVariable callId: Long,
        @RequestPart("updateData") @Valid updateRequest: ManagerUpdateCallRequestDTO,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?,
        @RequestParam("removeAttachmentIds", required = false) removeAttachmentIds: List<Long>?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.updateByManager(request, callId, updateRequest, attachments, removeAttachmentIds)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @PatchMapping("/{callId}/assign")
    fun assignCall(
        @PathVariable callId: Long,
        @RequestParam employeeId: Long,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.assignCallToEmployee(request, callId, employeeId)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @PostMapping("/{callId}/steps")
    fun addStepToCall(
        @PathVariable callId: Long,
        @RequestPart("stepData") @Valid stepRequest: CreateStepRequestDTO,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = stepService.create(callId, stepRequest, attachments)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @PatchMapping("/steps/{stepId}")
    fun updateStep(
        @PathVariable stepId: Long,
        @RequestPart("stepData") @Valid stepRequest: UpdateStepRequestDTO,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?,
        @RequestParam("removeAttachmentIds", required = false) removeAttachmentIds: List<Long>?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = stepService.update(stepId, stepRequest, attachments, removeAttachmentIds)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN, UserType.MUNICIPAL_EMPLOYEE)
    @PostMapping("/{callId}/interactions")
    fun addInteraction(
        @PathVariable callId: Long,
        @RequestBody @Valid interactionRequest: InteractionRequestDTO
    ): ResponseEntity<ApiResponseDTO> {
        val message =
            interactionService.createInteraction(callId, interactionRequest.updatedBy, interactionRequest.updateDetails)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.ADMIN)
    @DeleteMapping("/{callId}/interactions/{interactionId}")
    fun removeInteraction(
        @PathVariable callId: Long,
        @PathVariable interactionId: Long
    ): ResponseEntity<ApiResponseDTO> {
        val message = interactionService.deleteInteraction(callId, interactionId)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN, UserType.MUNICIPAL_EMPLOYEE, UserType.ADMIN)
    @GetMapping("/search")
    fun search(
        request: HttpServletRequest,
        @RequestParam(required = false) subject: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) statuses: List<CallStatus>?,
        @RequestParam(required = false) priorities: List<PriorityLevel>?,
        @RequestParam(required = false) citizenName: String?,
        @RequestParam(required = false) employeeName: String?,
        @RequestParam(required = false) categoryIds: List<Long>?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) createdAtStart: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) createdAtEnd: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) closedAtStart: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) closedAtEnd: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) estimatedCompletionStart: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) estimatedCompletionEnd: LocalDateTime?
    ): ResponseEntity<List<CallDetailsDTO>> {
        val callDetailsDTOs = callService.search(
            subject,
            description,
            statuses,
            priorities,
            citizenName,
            employeeName,
            categoryIds,
            createdAtStart,
            createdAtEnd,
            closedAtStart,
            closedAtEnd,
            estimatedCompletionStart,
            estimatedCompletionEnd
        )
        return ResponseEntity.ok(callDetailsDTOs)
    }
}