package br.upf.connect_city_api.util.constants.error

object GlobalExceptionHandlerConstants {
    const val MISSING_FIELDS_MESSAGE = "Um ou mais campos são nulos ou ausentes."
    const val UNRECOGNIZED_FIELD_MESSAGE = "Campo não reconhecido no JSON."
    const val JSON_READ_ERROR_MESSAGE = "Erro ao ler a mensagem JSON."
    const val UNKNOWN_ERROR = "Erro desconhecido"
    const val NO_DETAILS_AVAILABLE = "Sem detalhes disponíveis"
    const val FIELD_NOT_BLANK = "NotBlank"
    const val DUE_TO_MISSING = "due to missing (therefore NULL)"
    const val UNRECOGNIZED_FIELD = "Unrecognized field"
    const val FIELD_ERROR_FORMAT = "%s: %s"
    const val ERROR_JOIN_DELIMITER = ", "
    const val ERROR_JOIN_WITH_SPACE = ". "
    const val ERROR_POINT_FIX = ".,"
    const val POINT_END = "."
    const val DOUBLE_SPACE = "  "
    const val SINGLE_SPACE = " "
}
