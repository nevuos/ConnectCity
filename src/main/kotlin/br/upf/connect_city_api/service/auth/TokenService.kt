package br.upf.connect_city_api.service.auth

import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.service.user.UserAuthenticationService
import br.upf.connect_city_api.util.constants.auth.AuthClaims
import br.upf.connect_city_api.util.constants.auth.AuthCookies
import br.upf.connect_city_api.util.constants.auth.AuthMessages
import br.upf.connect_city_api.util.cookie.CookieUtil
import br.upf.connect_city_api.util.exception.AuthenticationError
import br.upf.connect_city_api.util.jwt.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val jwtUtil: JwtUtil,
    private val userAuthenticationService: UserAuthenticationService
) {

    fun getUserFromToken(token: String): User {
        val userId = jwtUtil.getClaim(token, AuthClaims.USER_ID_CLAIM)?.toLongOrNull()
            ?: throw AuthenticationError(AuthMessages.INVALID_TOKEN_CLAIMS)
        return userAuthenticationService.findById(userId)
    }

    fun getUserFromRequest(request: HttpServletRequest, cookieName: String = AuthCookies.ACCESS_TOKEN): User {
        val token = getTokenFromRequest(request, cookieName)
        return getUserFromToken(token)
    }

    fun getEmailFromToken(token: String): String? {
        return jwtUtil.getClaim(token, AuthClaims.EMAIL_CLAIM)
    }

    fun createAccessTokenWithUserId(user: User): String {
        return jwtUtil.generateAccessToken(AuthClaims.USER_ID_CLAIM, user.id)
    }

    fun createAccessTokenWithEmail(email: String): String {
        return jwtUtil.generateAccessToken(AuthClaims.EMAIL_CLAIM, email)
    }

    fun createRefreshToken(user: User): String {
        return jwtUtil.generateRefreshToken(AuthClaims.USER_ID_CLAIM, user.id)
    }

    fun validateToken(token: String): Boolean {
        return jwtUtil.isValidToken(token)
    }

    fun isValidToken(token: String?): Boolean {
        return token != null && validateToken(token)
    }

    fun getTokenExpiryTime(token: String?): Long? {
        return jwtUtil.getTokenExpirationTime(token)
    }

    internal fun getTokenFromRequest(
        request: HttpServletRequest,
        cookieName: String = AuthCookies.ACCESS_TOKEN
    ): String {
        val cookie = request.cookies?.find { it.name == cookieName }
            ?: throw AuthenticationError(AuthMessages.TOKEN_INVALID_OR_EXPIRED)
        return cookie.value
    }

    fun addTokenToResponse(
        token: String,
        cookieName: String = AuthCookies.ACCESS_TOKEN,
        response: HttpServletResponse
    ) {
        val maxAge = if (cookieName == AuthCookies.ACCESS_TOKEN) {
            AuthCookies.MAX_AGE_IN_SECONDS
        } else {
            AuthCookies.REFRESH_MAX_AGE_IN_SECONDS
        }
        val cookie = CookieUtil.create(cookieName, token, AuthCookies.PATH, maxAge)
        CookieUtil.add(response, cookie, AuthCookies.LAX)
    }

    fun clearTokensFromResponse(response: HttpServletResponse) {
        CookieUtil.remove(response, AuthCookies.ACCESS_TOKEN)
        CookieUtil.remove(response, AuthCookies.REFRESH_TOKEN)
    }

    fun generateAndSetTokens(user: User, response: HttpServletResponse) {
        val accessToken = createAccessTokenWithUserId(user)
        val refreshToken = createRefreshToken(user)

        addTokenToResponse(accessToken, AuthCookies.ACCESS_TOKEN, response)
        addTokenToResponse(refreshToken, AuthCookies.REFRESH_TOKEN, response)
    }
}