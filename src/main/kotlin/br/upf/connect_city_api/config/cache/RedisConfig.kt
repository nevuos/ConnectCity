package br.upf.connect_city_api.config.cache

import com.giffing.bucket4j.spring.boot.starter.config.cache.SyncCacheResolver
import com.giffing.bucket4j.spring.boot.starter.config.cache.jcache.JCacheCacheResolver
import io.github.bucket4j.distributed.proxy.ProxyManager
import io.github.bucket4j.grid.jcache.JCacheProxyManager
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.jcache.configuration.RedissonConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.cache.CacheManager
import javax.cache.Caching

@Configuration
class RedisConfig {

    @Value("\${spring.redis.host:localhost}")
    private lateinit var redisHost: String

    @Value("\${spring.redis.port:6379}")
    private var redisPort: Int = 6379

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress("redis://$redisHost:$redisPort")
        return Redisson.create(config)
    }

    @Bean
    fun cacheManager(redissonClient: RedissonClient): CacheManager {
        val manager = Caching.getCachingProvider().cacheManager
        manager.createCache("cache", RedissonConfiguration.fromInstance<Any, Any>(redissonClient))
        return manager
    }

    @Bean
    fun proxyManager(cacheManager: CacheManager): ProxyManager<String> {
        return JCacheProxyManager(cacheManager.getCache("cache"))
    }

    @Bean
    @Primary
    fun bucket4jCacheResolver(cacheManager: CacheManager?): SyncCacheResolver {
        return JCacheCacheResolver(cacheManager)
    }
}