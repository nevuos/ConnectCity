package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.email.EmailRequestDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.service.communication.EmailService
import br.upf.connect_city_api.util.security.UserTypeRequired
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/email")
class EmailController(private val emailService: EmailService) {

    @UserTypeRequired(UserType.MUNICIPAL_EMPLOYEE)
    @PostMapping("/send")
    fun send(@Valid @RequestBody requestDTO: EmailRequestDTO): CompletableFuture<ResponseEntity<ApiResponseDTO>> {
        val message = emailService.send(
            to = requestDTO.to,
            subject = requestDTO.subject,
            templateId = requestDTO.templateId,
        )
        return message.thenApply { msg -> ResponseEntity.ok(ApiResponseDTO(msg)) }
    }
}