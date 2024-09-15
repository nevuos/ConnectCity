package br.upf.connect_city_api.config.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CORSConfig {

    @Value("\${cors.allowed-origins}")
    private val allowedOrigins: List<String> = listOf()

    @Value("\${cors.allowed-methods}")
    private val allowedMethods: List<String> = listOf()

    @Value("\${cors.allowed-headers}")
    private val allowedHeaders: List<String> = listOf()

    @Value("\${cors.allow-credentials}")
    private val allowCredentials: Boolean = false

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = this@CORSConfig.allowedOrigins
            allowedMethods = this@CORSConfig.allowedMethods
            allowedHeaders = this@CORSConfig.allowedHeaders
            allowCredentials = this@CORSConfig.allowCredentials
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
        return source
    }
}