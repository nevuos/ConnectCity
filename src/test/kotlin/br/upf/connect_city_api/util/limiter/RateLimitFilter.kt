package br.upf.connect_city_api.util.limiter

import br.upf.connect_city_api.config.security.RateLimitConfig
import br.upf.connect_city_api.service.infrastructure.RateLimiterService
import io.github.bucket4j.Bucket
import io.github.bucket4j.ConsumptionProbe
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class RateLimitFilterTest {

    @MockBean
    private lateinit var rateLimitConfig: RateLimitConfig

    @MockBean
    private lateinit var rateLimiterService: RateLimiterService

    @MockBean
    private lateinit var redisTemplate: StringRedisTemplate

    @MockBean
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var rateLimitFilter: RateLimitFilter
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        rateLimitFilter = RateLimitFilter(rateLimitConfig, rateLimiterService)
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
    }

    // Teste para verificar se requisições abaixo do limite de taxa são permitidas.
    @Test
    fun `should pass when below the rate limit`() {
        val bucketMock: Bucket = mock()
        whenever(bucketMock.tryConsume(1)).thenReturn(true)
        whenever(rateLimitConfig.resolveBucket(request.remoteAddr)).thenReturn(bucketMock)
        whenever(valueOperations.get("block:${request.remoteAddr}")).thenReturn(null)

        val mockFilterChain: FilterChain = mock()

        rateLimitFilter.doFilter(request, response, mockFilterChain)

        assert(response.status != 429)
    }

    // Teste para verificar se requisições acima do limite de taxa são bloqueadas.
    @Test
    fun `should block when above the rate limit`() {
        val bucketMock: Bucket = mock()
        val probeMock: ConsumptionProbe = mock()

        whenever(probeMock.isConsumed).thenReturn(false)
        whenever(bucketMock.tryConsumeAndReturnRemaining(1)).thenReturn(probeMock)
        whenever(rateLimitConfig.resolveBucket(request.remoteAddr)).thenReturn(bucketMock)
        whenever(valueOperations.get("block:${request.remoteAddr}")).thenReturn(null)

        val mockFilterChain: FilterChain = mock()

        rateLimitFilter.doFilter(request, response, mockFilterChain)

        assert(response.status == 429)
    }
}