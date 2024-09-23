package br.upf.connect_city_api.util.error.mapping

import br.upf.connect_city_api.util.constants.error.ErrorMappingConstants
import br.upf.connect_city_api.util.constants.error.ErrorStatusConstants
import br.upf.connect_city_api.util.error.ErrorMapping

object StorageErrorMapping {
    val STORAGE_ERROR_INFO = ErrorMapping.ErrorInfo(
        ErrorMappingConstants.STORAGE_ERROR_CODE,
        ErrorMappingConstants.STORAGE_ERROR_MESSAGE,
        ErrorStatusConstants.STORAGE_ERROR_STATUS
    )
}