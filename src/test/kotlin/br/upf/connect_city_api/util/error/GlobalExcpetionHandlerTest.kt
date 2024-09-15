package br.upf.connect_city_api.util.error

import br.upf.connect_city_api.util.constants.error.GlobalExceptionHandlerConstants
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    /**
     * Teste para handleValidationExceptions.
     * Verifica se a exceção MethodArgumentNotValidException é tratada corretamente.
     * Espera-se um status BAD_REQUEST e a mensagem de erro detalhada com os campos inválidos.
     */
    @Test
    fun testHandleValidationExceptions() {
        val bindingResult = BeanPropertyBindingResult("target", "objectName")
        bindingResult.addError(FieldError("objectName", "field1", "Field1 is required"))
        bindingResult.addError(FieldError("objectName", "field2", "Field2 is invalid"))

        val mockMethodParameter = MethodParameter(GlobalExceptionHandler::class.java.methods.first(), -1)
        val exception = MethodArgumentNotValidException(mockMethodParameter, bindingResult)

        val response = handler.handleValidationExceptions(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorResponse = response.body!!
        assertEquals("VAL002", errorResponse.errorCode)
        assertEquals("Erro de validação", errorResponse.errorMessage)
        assertEquals("Bad Request", errorResponse.errorType)
        assertEquals(2, errorResponse.details.size)
        assertEquals("field1: Field1 is required", errorResponse.details[0])
        assertEquals("field2: Field2 is invalid", errorResponse.details[1])
    }

    /**
     * Teste para handleHttpMessageNotReadableExceptions.
     * Verifica se a exceção HttpMessageNotReadableException é tratada corretamente.
     * Espera-se um status BAD_REQUEST e a mensagem de erro apropriada.
     */
    @Test
    fun testHandleHttpMessageNotReadableExceptions() {
        val message = "Unrecognized field"
        val inputMessage: HttpInputMessage = object : HttpInputMessage {
            override fun getBody() = ByteArrayInputStream(message.toByteArray(StandardCharsets.UTF_8))
            override fun getHeaders() = HttpHeaders()
        }
        val exception = HttpMessageNotReadableException(message, inputMessage)

        val response = handler.handleHttpMessageNotReadableExceptions(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorResponse = response.body!!
        assertEquals("VAL003", errorResponse.errorCode)
        assertEquals("Erro ao ler a mensagem JSON", errorResponse.errorMessage)
        assertEquals("Bad Request", errorResponse.errorType)
        assertEquals(1, errorResponse.details.size)
        assertEquals(GlobalExceptionHandlerConstants.UNRECOGNIZED_FIELD_MESSAGE, errorResponse.details[0])
    }

    /**
     * Teste para handleAllExceptions.
     * Verifica se exceções genéricas são tratadas corretamente.
     * Espera-se um status INTERNAL_SERVER_ERROR e a mensagem de erro apropriada.
     */
    @Test
    fun testHandleAllExceptions() {
        val exception = RuntimeException("Unexpected error occurred")

        val response = handler.handleAllExceptions(exception)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        val errorResponse = response.body!!
        assertEquals("SYS001", errorResponse.errorCode)
        assertEquals("Erro inesperado", errorResponse.errorMessage)
        assertEquals("Internal Server Error", errorResponse.errorType)
        assertEquals(1, errorResponse.details.size)
        assertEquals("Unexpected error occurred", errorResponse.details[0])
    }

    /**
     * Teste para handleValidationExceptions com target nulo.
     * Verifica se a exceção MethodArgumentNotValidException é tratada corretamente
     * quando o target da bindingResult é nulo.
     */
    @Test
    fun testHandleValidationExceptionsWithNullTarget() {
        val bindingResult = BeanPropertyBindingResult(null, "objectName")
        bindingResult.addError(FieldError("objectName", "field1", "Field1 is required"))

        val mockMethodParameter = MethodParameter(GlobalExceptionHandler::class.java.methods.first(), -1)
        val exception = MethodArgumentNotValidException(mockMethodParameter, bindingResult)

        val response = handler.handleValidationExceptions(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorResponse = response.body!!
        assertEquals("VAL002", errorResponse.errorCode)
        assertEquals("Erro de validação", errorResponse.errorMessage)
        assertEquals("Bad Request", errorResponse.errorType)
        assertEquals(1, errorResponse.details.size)
        assertEquals("field1: Field1 is required", errorResponse.details[0])
    }

    /**
     * Teste para handleHttpMessageNotReadableExceptions com campos ausentes.
     * Verifica se a exceção HttpMessageNotReadableException é tratada corretamente
     * quando há campos ausentes na mensagem.
     */
    @Test
    fun testHandleHttpMessageNotReadableExceptionsWithMissingFields() {
        val message = GlobalExceptionHandlerConstants.DUE_TO_MISSING
        val inputMessage: HttpInputMessage = object : HttpInputMessage {
            override fun getBody() = ByteArrayInputStream(message.toByteArray(StandardCharsets.UTF_8))
            override fun getHeaders() = HttpHeaders()
        }
        val exception = HttpMessageNotReadableException(message, inputMessage)

        val response = handler.handleHttpMessageNotReadableExceptions(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorResponse = response.body!!
        assertEquals("VAL003", errorResponse.errorCode)
        assertEquals("Erro ao ler a mensagem JSON", errorResponse.errorMessage)
        assertEquals("Bad Request", errorResponse.errorType)
        assertEquals(1, errorResponse.details.size)
        assertEquals(GlobalExceptionHandlerConstants.MISSING_FIELDS_MESSAGE, errorResponse.details[0])
    }

    /**
     * Teste para handleHttpMessageNotReadableExceptions com campo não reconhecido.
     * Verifica se a exceção HttpMessageNotReadableException é tratada corretamente
     * quando há um campo não reconhecido na mensagem.
     */
    @Test
    fun testHandleHttpMessageNotReadableExceptionsWithUnrecognizedField() {
        val message = GlobalExceptionHandlerConstants.UNRECOGNIZED_FIELD
        val inputMessage: HttpInputMessage = object : HttpInputMessage {
            override fun getBody() = ByteArrayInputStream(message.toByteArray(StandardCharsets.UTF_8))
            override fun getHeaders() = HttpHeaders()
        }
        val exception = HttpMessageNotReadableException(message, inputMessage)

        val response = handler.handleHttpMessageNotReadableExceptions(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorResponse = response.body!!
        assertEquals("VAL003", errorResponse.errorCode)
        assertEquals("Erro ao ler a mensagem JSON", errorResponse.errorMessage)
        assertEquals("Bad Request", errorResponse.errorType)
        assertEquals(1, errorResponse.details.size)
        assertEquals(GlobalExceptionHandlerConstants.UNRECOGNIZED_FIELD_MESSAGE, errorResponse.details[0])
    }
}