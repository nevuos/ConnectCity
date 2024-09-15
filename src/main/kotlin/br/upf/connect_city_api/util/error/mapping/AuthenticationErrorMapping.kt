package br.upf.connect_city_api.util.error.mapping

import br.upf.connect_city_api.util.constants.error.ErrorMappingConstants
import br.upf.connect_city_api.util.constants.error.ErrorStatusConstants
import br.upf.connect_city_api.util.error.ErrorMapping

object AuthenticationErrorMapping {
    val ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.AUTHENTICATION_ERROR_CODE,
        ErrorMappingConstants.AUTHENTICATION_ERROR_MESSAGE,
        ErrorStatusConstants.AUTHENTICATION_ERROR_STATUS
    )
}
