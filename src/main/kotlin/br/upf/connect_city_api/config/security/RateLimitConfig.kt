package br.upf.connect_city_api.config.security

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.Refill
import io.github.bucket4j.distributed.proxy.ProxyManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RateLimitConfig(
    private val buckets: ProxyManager<String>
) {

    @Value("\${ratelimit.transactionsPerMinute:60}")
    private val transactionsPerMinute: Long = 60

    @Value("\${ratelimit.refillDurationMinutes:1}")
    private val refillDurationMinutes: Long = 1

    fun resolveBucket(key: String): Bucket {
        val refill = Refill.intervally(transactionsPerMinute, Duration.ofMinutes(refillDurationMinutes))
        val limit = Bandwidth.classic(transactionsPerMinute, refill)
        val configuration = BucketConfiguration.builder().addLimit(limit).build()

        return buckets.builder().build(key) { configuration }
    }
}