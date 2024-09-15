package br.upf.connect_city_api.service.infrastructure

import br.upf.connect_city_api.util.constants.auth.AuthCookies
import br.upf.connect_city_api.util.cookie.CookieUtil
import br.upf.connect_city_api.util.jwt.JwtUtil
import br.upf.connect_city_api.util.limiter.RateLimitFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class RateLimiterService(
    private val redisTemplate: StringRedisTemplate,
    private val jwtUtil: JwtUtil
) {

    @Value("\${ratelimit.blockDurationMinutes}")
    private val blockDurationMinutes: Long = 60

    @Value("\${ratelimit.violationThreshold}")
    private val violationThreshold: Int = 3

    @Value("\${ratelimit.hashAlgorithm:SHA-256}")
    private val hashAlgorithm: String = "SHA-256"

    companion object {
        private const val BLOCK_KEY_PREFIX = "block:"
        private const val SUSPECT_KEY_PREFIX = "suspect:"
        private const val RATE_LIMIT_EXCEEDED_STATUS = 429
        private const val HEADER_USER_AGENT = "User-Agent"
        private const val HEADER_SEC_CH_UA = "sec-ch-ua"
        private const val HEADER_SEC_CH_UA_MOBILE = "sec-ch-ua-mobile"
        private const val HEADER_SEC_CH_UA_PLATFORM = "sec-ch-ua-platform"
    }

    fun generateKey(request: HttpServletRequest): String {
        val ip = request.remoteAddr
        val userAgent = validateHeader(request.getHeader(HEADER_USER_AGENT))
        val secChUa = validateHeader(request.getHeader(HEADER_SEC_CH_UA))
        val secChUaMobile = validateHeader(request.getHeader(HEADER_SEC_CH_UA_MOBILE))
        val secChUaPlatform = validateHeader(request.getHeader(HEADER_SEC_CH_UA_PLATFORM))
        val accessToken = validateAccessToken(CookieUtil.getValue(request, AuthCookies.ACCESS_TOKEN))

        val keyComponents = listOf(ip, userAgent, secChUa, secChUaMobile, secChUaPlatform, accessToken)
            .filter { it != RateLimitFilter.UNKNOWN }
        val keyString = if (keyComponents.isEmpty()) ip else keyComponents.joinToString(":")
        return hashString(keyString)
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance(hashAlgorithm).digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isBlocked(key: String, request: HttpServletRequest, response: HttpServletResponse): Boolean {
        val blockKey = "$BLOCK_KEY_PREFIX$key"
        val blockUntilStr = redisTemplate.opsForValue().get(blockKey)
        val blockUntil = blockUntilStr?.let { Instant.parse(it) } ?: Instant.MIN

        if (Instant.now().isBefore(blockUntil)) {
            response.status = RATE_LIMIT_EXCEEDED_STATUS
            response.writer.write(RateLimitFilter.RATE_LIMIT_EXCEEDED_MESSAGE)
            return true
        } else {
            val ip = request.remoteAddr
            val suspectKey = "$SUSPECT_KEY_PREFIX$ip"
            val suspectCountStr = redisTemplate.opsForValue().get(suspectKey)
            val suspectCount = suspectCountStr?.toIntOrNull() ?: 0

            if (suspectCount >= violationThreshold) {
                blockIp(ip)
                response.status = RATE_LIMIT_EXCEEDED_STATUS
                response.writer.write(RateLimitFilter.RATE_LIMIT_EXCEEDED_MESSAGE)
                return true
            }
        }
        return false
    }

    @Async
    fun blockIp(ip: String): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val blockKey = "$BLOCK_KEY_PREFIX$ip"
            val blockUntil = Instant.now().plus(blockDurationMinutes, ChronoUnit.MINUTES)
            redisTemplate.opsForValue().set(blockKey, blockUntil.toString(), blockDurationMinutes, TimeUnit.MINUTES)
        }
    }

    @Async
    fun blockKey(key: String, request: HttpServletRequest, response: HttpServletResponse): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val blockKey = "$BLOCK_KEY_PREFIX$key"
            val blockUntil = Instant.now().plus(blockDurationMinutes, ChronoUnit.MINUTES)
            redisTemplate.opsForValue().set(blockKey, blockUntil.toString(), blockDurationMinutes, TimeUnit.MINUTES)

            val ip = request.remoteAddr
            markIpAsSuspect(ip)

            val refreshToken = CookieUtil.getValue(request, AuthCookies.REFRESH_TOKEN)
            if (!refreshToken.isNullOrBlank()) {
                CookieUtil.remove(response, AuthCookies.REFRESH_TOKEN)
            }
        }
    }

    @Async
    fun markIpAsSuspect(ip: String): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val suspectKey = "$SUSPECT_KEY_PREFIX$ip"
            val suspectCountStr = redisTemplate.opsForValue().get(suspectKey)
            val suspectCount = suspectCountStr?.toIntOrNull() ?: 0
            redisTemplate.opsForValue()
                .set(suspectKey, (suspectCount + 1).toString(), blockDurationMinutes, TimeUnit.MINUTES)
        }
    }

    private fun validateHeader(headerValue: String?): String {
        return headerValue ?: RateLimitFilter.UNKNOWN
    }

    private fun validateAccessToken(token: String?): String {
        return if (token != null) {
            try {
                jwtUtil.isValidToken(token)
                token
            } catch (e: Exception) {
                RateLimitFilter.UNKNOWN
            }
        } else {
            RateLimitFilter.UNKNOWN
        }
    }
}