package br.upf.connect_city_api.config.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ApiVersioningConfig : WebMvcConfigurer {

    @Value("\${api.prefix:/v1}")
    private lateinit var apiPrefix: String

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.addPathPrefix(apiPrefix) {
            it.isAnnotationPresent(RestController::class.java)
        }
    }
}
