package br.upf.connect_city_api.service.auth

import br.upf.connect_city_api.config.security.AccountLockoutConfig
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Service
class AccountLockoutService(
    private val redisTemplate: StringRedisTemplate,
    private val config: AccountLockoutConfig
) {

    companion object {
        private const val FAILED_ATTEMPTS_PREFIX = "failedAttempts:"
        private const val BLOCKED_ACCOUNT_PREFIX = "blockedAccount:"
        private const val PASSWORD_RESET_ATTEMPTS_PREFIX = "passwordResetAttempts:"
        private const val PASSWORD_RESET_BLOCKED_PREFIX = "passwordResetBlocked:"
    }

    fun isAccountBlocked(email: String): Boolean {
        val blockUntil = getBlockUntilTime("$BLOCKED_ACCOUNT_PREFIX$email")
        return Instant.now().isBefore(blockUntil)
    }

    fun isPasswordResetBlocked(email: String): Boolean {
        val blockUntil = getBlockUntilTime("$PASSWORD_RESET_BLOCKED_PREFIX$email")
        return Instant.now().isBefore(blockUntil)
    }

    @Async
    fun incrementFailedAttempts(email: String) {
        val attempts = incrementAttempts("$FAILED_ATTEMPTS_PREFIX$email")
        if (attempts >= config.maxFailedAttempts) {
            blockAccount(email)
        }
    }

    @Async
    fun incrementPasswordResetAttempts(email: String) {
        val attempts = incrementAttempts("$PASSWORD_RESET_ATTEMPTS_PREFIX$email")
        if (attempts >= config.maxFailedAttempts) {
            blockPasswordReset(email)
        }
    }

    private fun incrementAttempts(key: String): Int {
        val attempts = (redisTemplate.opsForValue().get(key)?.toIntOrNull() ?: 0) + 1
        redisTemplate.opsForValue().set(key, attempts.toString(), config.failedAttemptExpiryMinutes, TimeUnit.MINUTES)
        return attempts
    }

    @Async
    fun blockAccount(email: String) {
        blockUntil("$BLOCKED_ACCOUNT_PREFIX$email", config.blockDurationMinutes)
    }

    @Async
    fun blockPasswordReset(email: String) {
        blockUntil("$PASSWORD_RESET_BLOCKED_PREFIX$email", config.blockDurationMinutes)
    }

    @Async
    fun resetFailedAttempts(email: String) {
        redisTemplate.delete("$FAILED_ATTEMPTS_PREFIX$email")
    }

    @Async
    fun resetPasswordResetAttempts(email: String) {
        redisTemplate.delete("$PASSWORD_RESET_ATTEMPTS_PREFIX$email")
    }

    private fun blockUntil(key: String, durationMinutes: Long) {
        val blockUntil = Instant.now().plus(durationMinutes, ChronoUnit.MINUTES)
        redisTemplate.opsForValue().set(key, blockUntil.toString(), durationMinutes, TimeUnit.MINUTES)
    }

    private fun getBlockUntilTime(key: String): Instant {
        return redisTemplate.opsForValue().get(key)?.let { Instant.parse(it) } ?: Instant.MIN
    }
}