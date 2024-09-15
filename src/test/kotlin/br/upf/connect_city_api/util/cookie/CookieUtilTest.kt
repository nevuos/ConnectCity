package br.upf.connect_city_api.util.cookie

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class CookieUtilTest {

    private lateinit var mockRequest: HttpServletRequest
    private lateinit var mockResponse: HttpServletResponse

    @BeforeEach
    fun setUp() {
        mockRequest = mock()
        mockResponse = mock()
    }

    /**
     * Testa se createCookie cria um cookie com os valores corretos.
     * Verifica o nome, valor, caminho, maxAge, HttpOnly e Secure do cookie.
     */
    @Test
    fun `createCookie should create a cookie with correct values`() {
        val name = "testCookie"
        val value = "testValue"
        val path = "/testPath"
        val maxAgeInSeconds = 3600

        val cookie = CookieUtil.create(name, value, path, maxAgeInSeconds)

        assertEquals(name, cookie.name)
        assertEquals(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()), cookie.value)
        assertEquals(path, cookie.path)
        assertEquals(maxAgeInSeconds, cookie.maxAge)
        assertTrue(cookie.isHttpOnly)
        assertTrue(cookie.secure)
    }

    /**
     * Testa se addCookie adiciona um cookie à resposta HTTP corretamente.
     * Verifica se o cabeçalho 'Set-Cookie' é adicionado à resposta.
     */
    @Test
    fun `addCookie should add the cookie to the response`() {
        val cookie = Cookie("testCookie", "testValue")
        CookieUtil.add(mockResponse, cookie, "Lax")

        verify(mockResponse).addHeader(eq("Set-Cookie"), any())
    }

    /**
     * Testa se getCookieValue retorna o valor de um cookie especificado.
     * Verifica se o valor correto é retornado para um cookie existente.
     */
    @Test
    fun `getCookieValue should return the value of the specified cookie`() {
        val cookies = arrayOf(Cookie("testCookie", "testValue"))
        whenever(mockRequest.cookies).thenReturn(cookies)

        val value = CookieUtil.getValue(mockRequest, "testCookie")

        assertEquals("testValue", value)
    }

    /**
     * Testa se getCookieValue retorna null quando o cookie especificado não é encontrado.
     * Verifica se null é retornado para um nome de cookie inexistente.
     */
    @Test
    fun `getCookieValue should return null if cookie is not found`() {
        whenever(mockRequest.cookies).thenReturn(null)

        val value = CookieUtil.getValue(mockRequest, "nonExistingCookie")

        assertNull(value)
    }

    /**
     * Testa se createCookie lança IllegalArgumentException para nome de cookie vazio.
     * Verifica a mensagem de erro da exceção.
     */
    @Test
    fun `createCookie with empty name should throw IllegalArgumentException`() {
        val exception = assertThrows<IllegalArgumentException> {
            CookieUtil.create("", "value", "/path", 3600)
        }
        assertEquals("Cookie name may not be null or zero length", exception.message)
    }

    /**
     * Testa se createCookie cria um cookie com maxAge negativo.
     * Verifica se o valor negativo de maxAge é definido corretamente.
     */
    @Test
    fun `createCookie with negative maxAgeInSeconds should create a cookie with negative maxAge`() {
        val cookie = CookieUtil.create("name", "value", "/path", -1)
        assertEquals(-1, cookie.maxAge)
    }

    /**
     * Testa se addCookie adiciona um cookie com comportamento padrão de SameSite quando este é vazio.
     * Verifica se o cookie é adicionado sem especificar SameSite.
     */
    @Test
    fun `addCookie with empty sameSite should add the cookie with default sameSite behavior`() {
        val cookie = Cookie("testCookie", "testValue")
        CookieUtil.add(mockResponse, cookie, "")

        verify(mockResponse).addHeader(eq("Set-Cookie"), any())
    }

    /**
     * Testa se getCookieValue retorna null para um nome de cookie inexistente.
     * Verifica se null é retornado quando o cookie especificado não existe.
     */
    @Test
    fun `getCookieValue with non-existing cookie name should return null`() {
        val cookies = arrayOf(Cookie("testCookie", "testValue"))
        whenever(mockRequest.cookies).thenReturn(cookies)

        val value = CookieUtil.getValue(mockRequest, "nonExistingCookie")
        assertNull(value)
    }
}
