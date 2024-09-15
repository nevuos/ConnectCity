package br.upf.connect_city_api.util.limiter

import br.upf.connect_city_api.config.security.RateLimitConfig
import br.upf.connect_city_api.service.infrastructure.RateLimiterService
import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class RateLimitFilter @Autowired constructor(
    private val rateLimitConfig: RateLimitConfig, private val rateLimiterService: RateLimiterService
) : GenericFilterBean() {

    companion object {
        const val RATE_LIMIT_EXCEEDED_MESSAGE = "Número de requisições excedido. Tente novamente mais tarde."
        const val UNKNOWN = "unknown"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val key = rateLimiterService.generateKey(httpRequest)

        if (isRequestBlocked(key, httpRequest, httpResponse)) return

        val bucket: Bucket = rateLimitConfig.resolveBucket(key)
        if (!bucket.tryConsume(1)) {
            handleRateLimitExceeded(key, httpRequest, httpResponse)
            return
        }

        chain.doFilter(request, response)
    }

    private fun isRequestBlocked(key: String, request: HttpServletRequest, response: HttpServletResponse): Boolean {
        return rateLimiterService.isBlocked(key, request, response)
    }

    private fun handleRateLimitExceeded(key: String, request: HttpServletRequest, response: HttpServletResponse) {
        rateLimiterService.blockKey(key, request, response)
        response.status = 429
        response.writer.write(RATE_LIMIT_EXCEEDED_MESSAGE)
    }
}