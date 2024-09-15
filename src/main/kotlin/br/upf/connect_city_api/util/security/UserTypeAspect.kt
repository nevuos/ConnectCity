package br.upf.connect_city_api.util.security

import br.upf.connect_city_api.service.auth.PermissionService
import br.upf.connect_city_api.util.constants.auth.AuthMessages
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class UserTypeAspect(
    private val permissionService: PermissionService,
    private val request: HttpServletRequest
) {

    @Before("@annotation(userTypeRequired)")
    fun checkUserType(userTypeRequired: UserTypeRequired) {
        val requiredUserTypes = userTypeRequired.value
        if (!permissionService.hasAnyUserType(request, requiredUserTypes)) {
            throw PermissionDeniedError(AuthMessages.ACCESS_DENIED)
        }
    }
}