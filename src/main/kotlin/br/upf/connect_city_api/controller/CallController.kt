package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.call.CallDTO
import br.upf.connect_city_api.dtos.call.CreateCallRequestDTO
import br.upf.connect_city_api.dtos.call.UpdateCallRequestDTO
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.call.CallService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/call")
class CallController(
    private val callService: CallService,
) {

    @UserTypeRequired(UserType.CITIZEN)
    @PostMapping
    fun create(
        @Valid @RequestBody createRequest: CreateCallRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.create(request, createRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN)
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody updateRequest: UpdateCallRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponseDTO> {
        val message = callService.update(request, id, updateRequest)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN)
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<CallDTO> {
        val call = callService.getById(request, id)
        return ResponseEntity.ok(call)
    }

    @UserTypeRequired(UserType.CITIZEN)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<ApiResponseDTO> {
        val message = callService.delete(request, id)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @UserTypeRequired(UserType.CITIZEN)
    @GetMapping("/search")
    fun search(
        request: HttpServletRequest,
        @RequestParam(required = false) specs: Specification<Call>?
    ): ResponseEntity<List<CallDTO>> {
        val calls = callService.search(request, specs)
        return ResponseEntity.ok(calls)
    }
}