package br.upf.connect_city_api.util.exception

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

class CustomAsyncExceptionHandler : AsyncUncaughtExceptionHandler {
    private val logger = LoggerFactory.getLogger(CustomAsyncExceptionHandler::class.java)

    override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any?) {
        logger.error("Exception in method - ${method.name}: ${ex.message}")

        params.forEach { param ->
            if (param != null) {
                logger.error("Parameter value - {}", param)
            }
        }
    }
}