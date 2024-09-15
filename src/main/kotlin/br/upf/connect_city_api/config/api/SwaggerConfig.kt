package br.upf.connect_city_api.config.api

import org.springdoc.core.properties.SwaggerUiConfigProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

class SwaggerConfig {

    @Bean
    @Primary
    fun swaggerUiConfig(config: SwaggerUiConfigProperties): SwaggerUiConfigProperties {
        config.showCommonExtensions = true
        config.queryConfigEnabled = true
        return config
    }
}