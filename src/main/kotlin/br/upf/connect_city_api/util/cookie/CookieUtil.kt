package br.upf.connect_city_api.util.cookie

import br.upf.connect_city_api.util.constants.auth.AuthCookies
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object CookieUtil {

    private const val SET_COOKIE_HEADER = "Set-Cookie"
    private const val SECURE_ATTRIBUTE = "; Secure"
    private const val HTTP_ONLY_ATTRIBUTE = "; HttpOnly"
    private const val SAME_SITE_ATTRIBUTE = "; SameSite="
    private const val PATH_ATTRIBUTE = "; Path="
    private const val MAX_AGE_ATTRIBUTE = "; Max-Age="

    fun create(name: String, value: String, path: String, maxAgeInSeconds: Int): Cookie {
        val encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
        return Cookie(name, encodedValue).apply {
            this.path = path
            this.maxAge = maxAgeInSeconds
            this.isHttpOnly = true
            this.secure = true
        }
    }

    fun add(response: HttpServletResponse, cookie: Cookie, sameSite: String) {
        val cookieHeader = buildCookieHeader(cookie, sameSite)
        response.addHeader(SET_COOKIE_HEADER, cookieHeader)
    }

    fun remove(response: HttpServletResponse, cookieName: String) {
        val expiredCookie = create(cookieName, "", AuthCookies.PATH, 0)
        add(response, expiredCookie, AuthCookies.LAX)
    }

    private fun buildCookieHeader(cookie: Cookie, sameSite: String): String {
        val encodedValue = URLEncoder.encode(cookie.value, StandardCharsets.UTF_8.toString())
        return "${cookie.name}=$encodedValue" +
                PATH_ATTRIBUTE + cookie.path +
                MAX_AGE_ATTRIBUTE + cookie.maxAge +
                (if (cookie.secure) SECURE_ATTRIBUTE else "") +
                HTTP_ONLY_ATTRIBUTE +
                SAME_SITE_ATTRIBUTE + sameSite
    }

    fun getValue(request: HttpServletRequest, name: String): String? {
        return request.cookies?.firstOrNull { it.name == name }?.value
    }
}