package br.upf.connect_city_api.util.error

import br.upf.connect_city_api.util.error.mapping.*
import br.upf.connect_city_api.util.exception.*
import org.slf4j.LoggerFactory
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.servlet.resource.NoResourceFoundException

object ErrorMapping {

    data class ErrorInfo(val code: String, val message: String, val status: org.springframework.http.HttpStatus)

    private val logger = LoggerFactory.getLogger(ErrorMapping::class.java)

    private val exceptionToErrorInfoMap: Map<Class<out Throwable>, ErrorInfo> = mapOf(
        AuthenticationError::class.java to AuthenticationErrorMapping.ERROR_INFO,
        EmailAlreadyConfirmedError::class.java to EmailErrorMapping.EMAIL_ALREADY_CONFIRMED_ERROR_INFO,
        EmailSendError::class.java to EmailErrorMapping.EMAIL_SEND_ERROR_INFO,
        InvalidRequestError::class.java to RequestErrorMapping.INVALID_REQUEST_ERROR_INFO,
        InvalidUserTypeError::class.java to RequestErrorMapping.INVALID_REQUEST_ERROR_INFO,
        NoAuthorizationError::class.java to RequestErrorMapping.NO_AUTHORIZATION_ERROR_INFO,
        PermissionDeniedError::class.java to RequestErrorMapping.PERMISSION_DENIED_ERROR_INFO,
        ResourceNotFoundError::class.java to RequestErrorMapping.RESOURCE_NOT_FOUND_ERROR_INFO,
        NoResourceFoundException::class.java to RequestErrorMapping.RESOURCE_NOT_FOUND_ERROR_INFO,
        TimeoutError::class.java to RequestErrorMapping.TIMEOUT_ERROR_INFO,
        TooManyRequestsError::class.java to RequestErrorMapping.TOO_MANY_REQUESTS_ERROR_INFO,
        MethodArgumentNotValidException::class.java to ValidationErrorMapping.METHOD_ARGUMENT_NOT_VALID_ERROR_INFO,
        HttpMessageNotReadableException::class.java to ValidationErrorMapping.HTTP_MESSAGE_NOT_READABLE_ERROR_INFO,
        UnexpectedError::class.java to UnexpectedErrorMapping.ERROR_INFO
    )

    fun getErrorInfo(exception: Throwable): ErrorInfo {
        logger.debug("Mapeando exceção: ${exception::class.java.simpleName}")
        return exceptionToErrorInfoMap[exception::class.java]
            ?: UnexpectedErrorMapping.ERROR_INFO
    }
}