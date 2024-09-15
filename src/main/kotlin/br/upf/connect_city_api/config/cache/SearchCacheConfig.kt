package br.upf.connect_city_api.config.cache

import org.redisson.api.RedissonClient
import org.redisson.spring.cache.RedissonSpringCacheManager
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchCacheConfig {

    @Bean
    fun searchCacheManager(redissonClient: RedissonClient): CacheManager {
        return RedissonSpringCacheManager(redissonClient)
    }
}