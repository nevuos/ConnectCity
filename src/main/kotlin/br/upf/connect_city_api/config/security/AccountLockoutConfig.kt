package br.upf.connect_city_api.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AccountLockoutConfig(
    @Value("\${auth.blockDurationMinutes:60}")
    val blockDurationMinutes: Long = 60,

    @Value("\${auth.maxFailedAttempts:5}")
    val maxFailedAttempts: Int = 5,

    @Value("\${auth.failedAttemptExpiryMinutes:15}")
    val failedAttemptExpiryMinutes: Long = 15
)