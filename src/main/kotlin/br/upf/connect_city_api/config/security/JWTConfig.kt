package br.upf.connect_city_api.config.security

import br.upf.connect_city_api.util.constants.auth.AuthClaims
import br.upf.connect_city_api.util.constants.auth.AuthCookies
import br.upf.connect_city_api.util.jwt.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class JWTConfig(private val jwtUtil: JwtUtil) {

    @Bean
    fun jwtAuthenticationFilter(): OncePerRequestFilter {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(
                request: HttpServletRequest,
                response: HttpServletResponse,
                filterChain: FilterChain
            ) {
                val accessToken = request.cookies?.find { it.name == AuthCookies.ACCESS_TOKEN }?.value

                if (accessToken != null && jwtUtil.isValidToken(accessToken)) {
                    val email = jwtUtil.getClaim(accessToken, AuthClaims.EMAIL_CLAIM)
                    val authentication = UsernamePasswordAuthenticationToken(email, null, emptyList())
                    SecurityContextHolder.getContext().authentication = authentication
                }

                filterChain.doFilter(request, response)
            }
        }
    }
}
