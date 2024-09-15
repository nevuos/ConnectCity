package br.upf.connect_city_api.config.async

import br.upf.connect_city_api.util.exception.CustomAsyncExceptionHandler
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig(
    private val threadPoolTaskExecutorConfig: ThreadPoolTaskExecutorConfig
) : AsyncConfigurer {

    @Bean(name = ["taskExecutor"])
    override fun getAsyncExecutor(): Executor {
        return threadPoolTaskExecutorConfig.createExecutor()
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return CustomAsyncExceptionHandler()
    }
}