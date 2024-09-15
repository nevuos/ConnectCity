package br.upf.connect_city_api.config.async

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class ThreadPoolTaskExecutorConfig {

    @Value("\${async.corePoolSize:5}")
    private val corePoolSize: Int = 5

    @Value("\${async.maxPoolSize:10}")
    private val maxPoolSize: Int = 10

    @Value("\${async.queueCapacity:100}")
    private val queueCapacity: Int = 100

    @Value("\${async.threadNamePrefix:Async-}")
    private val threadNamePrefix: String = "Async-"

    fun createExecutor(): ThreadPoolTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            this.corePoolSize = this@ThreadPoolTaskExecutorConfig.corePoolSize
            this.maxPoolSize = this@ThreadPoolTaskExecutorConfig.maxPoolSize
            this.queueCapacity = this@ThreadPoolTaskExecutorConfig.queueCapacity
            setThreadNamePrefix(this@ThreadPoolTaskExecutorConfig.threadNamePrefix)
            initialize()
        }
    }
}