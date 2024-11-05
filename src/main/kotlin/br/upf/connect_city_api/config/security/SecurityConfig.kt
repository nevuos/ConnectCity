package br.upf.connect_city_api.config.security

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtConfig: JWTConfig,
    private val cspFilter: CSPFilter,
    private val corsConfigurationSource: CorsConfigurationSource
) {

    @Value("\${api.prefix:/v1}")
    private lateinit var apiPrefix: String

    private lateinit var authWhitelist: Array<String>

    @PostConstruct
    fun init() {
        authWhitelist = arrayOf(
            "$apiPrefix/swagger-ui/**",
            "$apiPrefix/v3/api-docs/**",
            "$apiPrefix/auth/register",
            "$apiPrefix/auth/login",
            "$apiPrefix/auth/logout",
            "$apiPrefix/auth/confirm-email/**",
            "$apiPrefix/auth/resend-confirmation",
            "$apiPrefix/auth/request-password-reset",
            "$apiPrefix/auth/reset-password"
        )
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity, firewall: HttpFirewall): SecurityFilterChain {
        configureCsrf(http)
        configureCors(http)
        configureFilters(http)
        configureAuthorization(http)
        return http.build()
    }

    private fun configureCsrf(http: HttpSecurity) {
        val requestHandler = CsrfTokenRequestAttributeHandler()
        http.csrf { csrf ->
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers(*authWhitelist)
        }
    }

    private fun configureCors(http: HttpSecurity) {
        http.cors { cors ->
            cors.configurationSource(corsConfigurationSource)
        }
    }

    private fun configureFilters(http: HttpSecurity) {
        http.addFilterBefore(jwtConfig.jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(cspFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    private fun configureAuthorization(http: HttpSecurity) {
        http.authorizeHttpRequests { authorize ->
            authorize
                .requestMatchers(*authWhitelist).permitAll()
                .anyRequest().authenticated()
        }
    }

    @Bean
    fun webSecurityCustomizer(firewall: HttpFirewall): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.httpFirewall(firewall)
        }
    }
}