package br.upf.connect_city_api.util.error.mapping

import br.upf.connect_city_api.util.constants.error.ErrorMappingConstants
import br.upf.connect_city_api.util.constants.error.ErrorStatusConstants
import br.upf.connect_city_api.util.error.ErrorMapping

object UnexpectedErrorMapping {
    val ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.UNEXPECTED_ERROR_CODE,
        ErrorMappingConstants.UNEXPECTED_ERROR_MESSAGE,
        ErrorStatusConstants.UNEXPECTED_ERROR_STATUS
    )
}