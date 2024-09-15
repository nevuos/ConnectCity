package br.upf.connect_city_api.util.jwt

import br.upf.connect_city_api.service.auth.TokenService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import org.junit.jupiter.api.AfterEach


@ExtendWith(MockitoExtension::class)
class JwtUtilTest {

    @InjectMocks
    private lateinit var jwtUtil: JwtUtil

    @InjectMocks
    private lateinit var tokenService: TokenService

    private val secretKey = "testSecretKey"
    private val accessTokenExpirationMs = 600000L // 10 minutos
    private val refreshTokenExpirationMs = 1200000L // 20 minutos

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey)
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpirationMs", accessTokenExpirationMs)
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpirationMs", refreshTokenExpirationMs)
    }

    /**
     * Verifica se o método createAccessToken cria um token de acesso válido.
     * Testa se o token não é nulo e contém a reivindicação especificada.
     */
    @Test
    fun `createAccessToken should create a valid access token`() {
        val claim = "email"
        val value = "test@example.com"

        val token = jwtUtil.generateAccessToken(claim, value)
        assertNotNull(token)

        val decodedToken: DecodedJWT = JWT.decode(token)
        assertEquals(value, decodedToken.getClaim(claim).asString())
    }

    /**
     * Verifica se o método createRefreshToken cria um token de atualização válido.
     * Testa se o token não é nulo e contém a reivindicação especificada.
     */
    @Test
    fun `createRefreshToken should create a valid refresh token`() {
        val claim = "email"
        val value = "test@example.com"

        val token = jwtUtil.generateRefreshToken(claim, value)
        assertNotNull(token)

        val decodedToken: DecodedJWT = JWT.decode(token)
        assertEquals(value, decodedToken.getClaim(claim).asString())
    }

    /**
     * Verifica se o método validateToken retorna verdadeiro para um token válido e falso para um inválido.
     * Testa a função de validação do token.
     */
    @Test
    fun `validateToken should return true for valid token and false for invalid`() {
        val validToken = jwtUtil.generateAccessToken("email", "test@example.com")
        assertTrue(jwtUtil.isValidToken(validToken))

        val invalidToken = "invalidToken"
        assertFalse(jwtUtil.isValidToken(invalidToken))
    }

    /**
     * Verifica se o método getTokenExpiryTime retorna o tempo restante até a expiração do token.
     * Testa se o tempo retornado é maior que zero para um token válido.
     */
    @Test
    fun `getTokenExpiryTime should return remaining time until token expiration`() {
        val token = jwtUtil.generateAccessToken("email", "test@example.com")
        val timeRemaining = jwtUtil.getTokenExpirationTime(token)
        assertNotNull(timeRemaining)
        assertTrue(timeRemaining!! > 0)
    }

    /**
     * Verifica se o método getClaimFromToken extrai corretamente a reivindicação especificada de um token.
     * Testa se a reivindicação extraída corresponde ao valor esperado.
     */
    @Test
    fun `getClaimFromToken should extract specified claim from token`() {
        val claim = "email"
        val value = "test@example.com"
        val token = jwtUtil.generateAccessToken(claim, value)

        val extractedValue = jwtUtil.getClaim(token, claim)
        assertEquals(value, extractedValue)
    }

    /**
     * Verifica se o método getEmailFromToken extrai corretamente o email de um token.
     * Testa se o email extraído corresponde ao valor esperado.
     */
    @Test
    fun `getEmailFromToken should extract email from token`() {
        val email = "test@example.com"
        val token = jwtUtil.generateAccessToken("email", email)

        val extractedEmail = tokenService.getEmailFromToken(token)
        assertEquals(email, extractedEmail)
    }

    /**
     * Verifica se o método createAccessToken produz um token que falha na validação quando criado com uma chave secreta inválida.
     * Espera-se que o token criado com uma chave secreta inválida não seja validável com a chave secreta correta.
     */
    @Test
    fun `createAccessToken with invalid secret key should produce unverifiable token`() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "invalidSecretKey")
        val claim = "email"
        val value = "test@example.com"

        val invalidToken = jwtUtil.generateAccessToken(claim, value)
        assertNotNull(invalidToken)

        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey)
        assertFalse(jwtUtil.isValidToken(invalidToken))
    }


    /**
     * Verifica se o método validateToken retorna falso para um token com assinatura inválida.
     * Um token com assinatura alterada deve ser considerado inválido.
     */
    @Test
    fun `validateToken should return false for token with invalid signature`() {
        val validToken = jwtUtil.generateAccessToken("email", "test@example.com")
        val alteredToken = validToken + "alteredPart"

        assertFalse(jwtUtil.isValidToken(alteredToken))
    }

    /**
     * Verifica se o método getTokenExpiryTime retorna null para um token inválido.
     * Espera-se que um token malformado ou corrompido retorne null.
     */
    @Test
    fun `getTokenExpiryTime should return null for invalid token`() {
        val invalidToken = "invalidToken"

        assertNull(jwtUtil.getTokenExpirationTime(invalidToken))
    }

    /**
     * Verifica se o método getClaimFromToken retorna null para um token sem a reivindicação especificada.
     * Um token sem a reivindicação desejada deve resultar em null.
     */
    @Test
    fun `getClaimFromToken should return null for token without specified claim`() {
        val token = jwtUtil.generateAccessToken("differentClaim", "value")

        assertNull(jwtUtil.getClaim(token, "email"))
    }

    /**
     * Verifica se o método getEmailFromToken retorna null para um token sem a reivindicação de email.
     * Espera-se que um token sem a reivindicação de email retorne null.
     */
    @Test
    fun `getEmailFromToken should return null for token without email claim`() {
        val token = jwtUtil.generateAccessToken("differentClaim", "value")

        assertNull(tokenService.getEmailFromToken(token))
    }

    @AfterEach
    fun tearDown() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey)
    }

}
