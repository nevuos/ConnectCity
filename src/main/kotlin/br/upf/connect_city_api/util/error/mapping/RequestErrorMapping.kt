package br.upf.connect_city_api.util.error.mapping

import br.upf.connect_city_api.util.constants.error.ErrorMappingConstants
import br.upf.connect_city_api.util.constants.error.ErrorStatusConstants
import br.upf.connect_city_api.util.error.ErrorMapping

object RequestErrorMapping {
    val INVALID_REQUEST_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.INVALID_REQUEST_ERROR_CODE,
        ErrorMappingConstants.INVALID_REQUEST_ERROR_MESSAGE,
        ErrorStatusConstants.INVALID_REQUEST_ERROR_STATUS
    )

    val NO_AUTHORIZATION_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.NO_AUTHORIZATION_ERROR_CODE,
        ErrorMappingConstants.NO_AUTHORIZATION_ERROR_MESSAGE,
        ErrorStatusConstants.NO_AUTHORIZATION_ERROR_STATUS
    )

    val PERMISSION_DENIED_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.PERMISSION_DENIED_ERROR_CODE,
        ErrorMappingConstants.PERMISSION_DENIED_ERROR_MESSAGE,
        ErrorStatusConstants.PERMISSION_DENIED_ERROR_STATUS
    )

    val RESOURCE_NOT_FOUND_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.RESOURCE_NOT_FOUND_ERROR_CODE,
        ErrorMappingConstants.RESOURCE_NOT_FOUND_ERROR_MESSAGE,
        ErrorStatusConstants.RESOURCE_NOT_FOUND_ERROR_STATUS
    )

    val TIMEOUT_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.TIMEOUT_ERROR_CODE,
        ErrorMappingConstants.TIMEOUT_ERROR_MESSAGE,
        ErrorStatusConstants.TIMEOUT_ERROR_STATUS
    )

    val TOO_MANY_REQUESTS_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.TOO_MANY_REQUESTS_ERROR_CODE,
        ErrorMappingConstants.TOO_MANY_REQUESTS_ERROR_MESSAGE,
        ErrorStatusConstants.TOO_MANY_REQUESTS_ERROR_STATUS
    )
}