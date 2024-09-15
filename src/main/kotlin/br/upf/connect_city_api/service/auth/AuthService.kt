package br.upf.connect_city_api.service.auth

import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.service.communication.EmailService
import br.upf.connect_city_api.service.user.UserAuthenticationService
import br.upf.connect_city_api.util.constants.auth.AuthCookies
import br.upf.connect_city_api.util.constants.auth.AuthMessages
import br.upf.connect_city_api.util.constants.auth.AuthTemplates
import br.upf.connect_city_api.util.constants.auth.AuthUrls
import br.upf.connect_city_api.util.exception.AuthenticationError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userAuthenticationService: UserAuthenticationService,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val accountLockoutService: AccountLockoutService,
    private val tokenService: TokenService
) {

    fun register(username: String, password: String, email: String): String {
        userAuthenticationService.register(username, password, email)
        sendConfirmationEmail(email)
        return AuthMessages.EMAIL_CONFIRMATION_MESSAGE
    }

    fun login(email: String, password: String, response: HttpServletResponse): String {
        val user = findUserForLogin(email)
        validateUserCredentials(user, password)
        tokenService.generateAndSetTokens(user, response)
        return AuthMessages.LOGIN_SUCCESS_MESSAGE
    }

    fun requestPasswordReset(email: String): String {
        if (accountLockoutService.isPasswordResetBlocked(email)) {
            throw AuthenticationError(AuthMessages.PASSWORD_RESET_REQUEST_LIMIT_REACHED)
        }

        val user = try {
            userAuthenticationService.findByEmail(email)
        } catch (e: ResourceNotFoundError) {
            return AuthMessages.PASSWORD_RESET_REQUEST_ACCEPTED
        }

        if (user.isActive) {
            accountLockoutService.incrementPasswordResetAttempts(email)
            sendPasswordResetEmail(email)
        }

        return AuthMessages.PASSWORD_RESET_REQUEST_ACCEPTED
    }

    fun resetPassword(token: String, newPassword: String): String {
        val email = tokenService.getEmailFromToken(token)
            ?: throw AuthenticationError(AuthMessages.TOKEN_INVALID_OR_EXPIRED)

        val user = userAuthenticationService.findByEmail(email)
        userAuthenticationService.updatePassword(user.id, newPassword)

        accountLockoutService.resetPasswordResetAttempts(email)
        return AuthMessages.PASSWORD_CHANGED_SUCCESS
    }

    fun validateToken(token: String?): String {
        if (!tokenService.isValidToken(token)) {
            throw AuthenticationError(AuthMessages.TOKEN_INVALID_OR_EXPIRED)
        }
        return AuthMessages.TOKEN_VALID
    }

    fun logout(response: HttpServletResponse): String {
        tokenService.clearTokensFromResponse(response)
        return AuthMessages.LOGOUT_SUCCESS_MESSAGE
    }

    fun renewAccessToken(request: HttpServletRequest, response: HttpServletResponse): String {
        val refreshToken = tokenService.getTokenFromRequest(request, AuthCookies.REFRESH_TOKEN)

        if (!tokenService.validateToken(refreshToken)) {
            throw AuthenticationError(AuthMessages.INVALID_REFRESH_TOKEN)
        }

        val user = tokenService.getUserFromToken(refreshToken)

        val newAccessToken = tokenService.createAccessTokenWithUserId(user)
        tokenService.addTokenToResponse(newAccessToken, AuthCookies.ACCESS_TOKEN, response)

        return AuthMessages.ACCESS_TOKEN_RENEWED
    }

    fun getTokenExpiryTime(token: String?): Long {
        return tokenService.getTokenExpiryTime(token)
            ?: throw AuthenticationError(AuthMessages.TOKEN_INVALID_OR_EXPIRED)
    }

    fun confirmEmail(token: String, emailConfirmed: Boolean): String {
        val email = tokenService.getEmailFromToken(token)
            ?: throw AuthenticationError(AuthMessages.TOKEN_INVALID_OR_EXPIRED)
        userAuthenticationService.confirmEmail(email, emailConfirmed)
        return AuthMessages.EMAIL_CONFIRMED_MESSAGE
    }

    fun resendConfirmation(email: String): String {
        userAuthenticationService.verifyForResendConfirmation(email)
        sendConfirmationEmail(email)
        return AuthMessages.EMAIL_CONFIRMATION_MESSAGE
    }

    private fun findUserForLogin(email: String): User {
        return try {
            userAuthenticationService.findByEmail(email)
        } catch (e: ResourceNotFoundError) {
            accountLockoutService.incrementFailedAttempts(email)
            throw AuthenticationError(AuthMessages.INVALID_CREDENTIALS)
        }
    }

    private fun validateUserCredentials(user: User, password: String) {
        if (accountLockoutService.isAccountBlocked(user.email)) {
            throw AuthenticationError(AuthMessages.ACCOUNT_LOCKED_MESSAGE)
        }

        if (!passwordEncoder.matches(password, user.password)) {
            accountLockoutService.incrementFailedAttempts(user.email)
            throw AuthenticationError(AuthMessages.INVALID_CREDENTIALS)
        }

        if (!user.isActive) {
            throw AuthenticationError(AuthMessages.ACCOUNT_INACTIVE)
        }

        if (!userAuthenticationService.isEmailConfirmed(user.id)) {
            throw AuthenticationError(AuthMessages.EMAIL_PENDING_CONFIRMATION)
        }

        if (!accountLockoutService.isAccountBlocked(user.email)) {
            accountLockoutService.resetFailedAttempts(user.email)
        }
    }

    private fun sendConfirmationEmail(email: String) {
        val token = tokenService.createAccessTokenWithEmail(email)
        val completeConfirmationUrl = AuthUrls.CONFIRMATION_URL + token

        emailService.send(
            to = email,
            subject = AuthMessages.CONFIRMATION_SUBJECT,
            confirmationUrl = completeConfirmationUrl,
            templateId = AuthTemplates.EMAIL_CONFIRMATION_TEMPLATE
        )
    }

    private fun sendPasswordResetEmail(email: String) {
        val token = tokenService.createAccessTokenWithEmail(email)
        val completePasswordResetUrl = AuthUrls.PASSWORD_RESET_URL + token

        emailService.send(
            to = email,
            subject = AuthMessages.RESET_SUBJECT,
            confirmationUrl = completePasswordResetUrl,
            templateId = AuthTemplates.PASSWORD_RESET_TEMPLATE
        )
    }
}