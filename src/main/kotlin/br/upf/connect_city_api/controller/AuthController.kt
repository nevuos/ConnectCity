package br.upf.connect_city_api.controller

import br.upf.connect_city_api.dtos.api.ApiResponseDTO
import br.upf.connect_city_api.dtos.auth.*
import br.upf.connect_city_api.service.auth.AuthService
import br.upf.connect_city_api.service.auth.TokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val tokenService: TokenService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody requestBody: RegisterRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponseDTO> {
        val message = authService.register(requestBody.username, requestBody.password, requestBody.email)
        val responseDTO = ApiResponseDTO(message)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody requestBody: LoginRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponseDTO> {
        val message = authService.login(requestBody.email, requestBody.password, response)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @GetMapping("/validate-token")
    fun validateToken(request: HttpServletRequest): ResponseEntity<ApiResponseDTO> {
        val token = tokenService.getTokenFromRequest(request)
        val message = authService.validateToken(token)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<ApiResponseDTO> {
        val message = authService.logout(response)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @PostMapping("/renew-token")
    fun renewAccessToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<ApiResponseDTO> {
        val message = authService.renewAccessToken(request, response)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @GetMapping("/token-expiry")
    fun getTokenExpiry(request: HttpServletRequest): ResponseEntity<TokenExpiryResponseDTO> {
        val token = tokenService.getTokenFromRequest(request)
        val timeRemaining = authService.getTokenExpiryTime(token)
        val responseDTO = TokenExpiryResponseDTO(timeRemaining)
        return ResponseEntity.ok(responseDTO)
    }

    @GetMapping("/confirm-email/{token}")
    fun confirmEmail(@PathVariable token: String): ResponseEntity<ApiResponseDTO> {
        val message = authService.confirmEmail(token, true)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @PostMapping("/resend-confirmation")
    fun resendConfirmation(@Valid @RequestBody requestBody: ResendConfirmationRequestDTO): ResponseEntity<ApiResponseDTO> {
        val message = authService.resendConfirmation(requestBody.email)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @PostMapping("/request-password-reset")
    fun requestPasswordReset(@Valid @RequestBody requestBody: RequestPasswordResetDTO): ResponseEntity<ApiResponseDTO> {
        val message = authService.requestPasswordReset(requestBody.email)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }

    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody requestBody: ResetPasswordRequestDTO): ResponseEntity<ApiResponseDTO> {
        val message = authService.resetPassword(requestBody.token, requestBody.newPassword)
        return ResponseEntity.ok(ApiResponseDTO(message))
    }
}