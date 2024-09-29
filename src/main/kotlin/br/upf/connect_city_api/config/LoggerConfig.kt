package br.upf.connect_city_api.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfig {

    @Bean
    fun logger(): Logger {
        return LoggerFactory.getLogger(LoggerConfig::class.java)
    }

    companion object {
        fun getLogger(forClass: Class<*>): Logger {
            return LoggerFactory.getLogger(forClass)
        }
    }
}