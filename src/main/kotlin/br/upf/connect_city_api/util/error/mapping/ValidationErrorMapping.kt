package br.upf.connect_city_api.util.error.mapping

import br.upf.connect_city_api.util.constants.error.ErrorMappingConstants
import br.upf.connect_city_api.util.constants.error.ErrorStatusConstants
import br.upf.connect_city_api.util.error.ErrorMapping

object ValidationErrorMapping {
    val METHOD_ARGUMENT_NOT_VALID_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.VALIDATION_ERROR_CODE,
        ErrorMappingConstants.VALIDATION_ERROR_MESSAGE,
        ErrorStatusConstants.VALIDATION_ERROR_STATUS
    )

    val HTTP_MESSAGE_NOT_READABLE_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.JSON_MESSAGE_READ_ERROR_CODE,
        ErrorMappingConstants.JSON_MESSAGE_READ_ERROR_MESSAGE,
        ErrorStatusConstants.JSON_MESSAGE_READ_ERROR_STATUS
    )
}