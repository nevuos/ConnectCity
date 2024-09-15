package br.upf.connect_city_api.config.mapper

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()

        modelMapper.configuration
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(AccessLevel.PRIVATE)
            .isSkipNullEnabled = true

        return modelMapper
    }
}