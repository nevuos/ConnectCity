package br.upf.connect_city_api.util.error.mapping

import br.upf.connect_city_api.util.constants.error.ErrorMappingConstants
import br.upf.connect_city_api.util.constants.error.ErrorStatusConstants
import br.upf.connect_city_api.util.error.ErrorMapping


object EmailErrorMapping {
    val EMAIL_ALREADY_CONFIRMED_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.EMAIL_ALREADY_CONFIRMED_ERROR_CODE,
        ErrorMappingConstants.EMAIL_ALREADY_CONFIRMED_ERROR_MESSAGE,
        ErrorStatusConstants.EMAIL_ALREADY_CONFIRMED_ERROR_STATUS
    )

    val EMAIL_SEND_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.EMAIL_SEND_ERROR_CODE,
        ErrorMappingConstants.EMAIL_SEND_ERROR_MESSAGE,
        ErrorStatusConstants.EMAIL_SEND_ERROR_STATUS
    )
}