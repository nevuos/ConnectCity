package br.upf.connect_city_api.config.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        return builder.createXmlMapper(false)
            .build<ObjectMapper>()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
            .registerModule(trimAndSanitizeStringModule())
    }

    private fun trimAndSanitizeStringModule(): SimpleModule {
        return SimpleModule().apply {
            addDeserializer(String::class.java, SanitizingStringDeserializer())
        }
    }
}