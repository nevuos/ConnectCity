package br.upf.connect_city_api.service.auth

import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.util.constants.auth.AuthMessages
import br.upf.connect_city_api.util.exception.NoAuthorizationError
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
class PermissionService(
    private val tokenService: TokenService
) {

    fun hasAnyUserType(request: HttpServletRequest, userTypes: Array<out UserType>): Boolean {
        val user = try {
            tokenService.getUserFromRequest(request)
        } catch (e: Exception) {
            throw NoAuthorizationError(AuthMessages.USER_NOT_AUTHENTICATED)
        }

        if (user.userType !in userTypes) {
            throw PermissionDeniedError(AuthMessages.ACCESS_DENIED)
        }

        return true
    }
}