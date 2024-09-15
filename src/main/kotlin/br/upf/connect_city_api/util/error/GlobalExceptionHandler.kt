package br.upf.connect_city_api.util.error

import br.upf.connect_city_api.model.error.ErrorResponse
import br.upf.connect_city_api.util.constants.error.GlobalExceptionHandlerConstants
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorInfo = ErrorMapping.getErrorInfo(ex)

        val targetClass = ex.bindingResult.target?.javaClass?.kotlin
        val fieldOrder = targetClass?.memberProperties?.map { it.name } ?: emptyList()

        val fieldErrorsMap = ex.bindingResult.fieldErrors.groupBy { it.field }

        val sortedFieldErrorsMap = fieldErrorsMap.mapValues { (_, errors) ->
            errors.sortedBy { error -> if (error.codes?.contains(GlobalExceptionHandlerConstants.FIELD_NOT_BLANK) == true) 0 else 1 }
        }

        val errorDetails = buildErrorDetails(fieldOrder, sortedFieldErrorsMap)

        return errorDetails.buildErrorResponse(errorInfo)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableExceptions(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val errorInfo = ErrorMapping.getErrorInfo(ex)
        val details = when {
            ex.message?.contains(GlobalExceptionHandlerConstants.DUE_TO_MISSING) == true -> {
                listOf(GlobalExceptionHandlerConstants.MISSING_FIELDS_MESSAGE)
            }

            ex.message?.contains(GlobalExceptionHandlerConstants.UNRECOGNIZED_FIELD) == true -> {
                listOf(GlobalExceptionHandlerConstants.UNRECOGNIZED_FIELD_MESSAGE)
            }

            else -> listOf(GlobalExceptionHandlerConstants.JSON_READ_ERROR_MESSAGE)
        }
        return details.buildErrorResponse(errorInfo)
    }

    @ExceptionHandler(Throwable::class)
    fun handleAllExceptions(ex: Throwable): ResponseEntity<ErrorResponse> {
        val errorInfo = ErrorMapping.getErrorInfo(ex)
        val details = listOf(ex.message ?: GlobalExceptionHandlerConstants.NO_DETAILS_AVAILABLE)
        return details.buildErrorResponse(errorInfo)
    }

    private fun buildErrorDetails(
        fieldOrder: List<String>,
        sortedFieldErrorsMap: Map<String, List<FieldError>>
    ): List<String?> {
        val orderedErrors: List<String> = fieldOrder.mapNotNull { field ->
            sortedFieldErrorsMap[field]?.let { errors ->
                String.format(
                    GlobalExceptionHandlerConstants.FIELD_ERROR_FORMAT,
                    field,
                    errors.joinToString(GlobalExceptionHandlerConstants.ERROR_JOIN_DELIMITER) {
                        it.defaultMessage ?: GlobalExceptionHandlerConstants.UNKNOWN_ERROR
                    })
            }
        }
        val remainingErrors = sortedFieldErrorsMap.keys
            .filterNot { fieldOrder.contains(it) }
            .map { field ->
                sortedFieldErrorsMap[field]?.let { errors ->
                    String.format(
                        GlobalExceptionHandlerConstants.FIELD_ERROR_FORMAT,
                        field,
                        errors.joinToString(GlobalExceptionHandlerConstants.ERROR_JOIN_DELIMITER) {
                            it.defaultMessage ?: GlobalExceptionHandlerConstants.UNKNOWN_ERROR
                        })
                }
            }
        return orderedErrors + remainingErrors
    }

    private fun List<String?>.buildErrorResponse(errorInfo: ErrorMapping.ErrorInfo): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            errorCode = errorInfo.code,
            errorMessage = errorInfo.message,
            errorType = errorInfo.status.reasonPhrase,
            details = this.filterNotNull(),
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity(errorResponse, errorInfo.status)
    }
}