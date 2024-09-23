package br.upf.connect_city_api.util.constants.error

object ErrorMappingConstants {
    // Error Codes
    const val AUTHENTICATION_ERROR_CODE = "AUTH001"
    const val EMAIL_SEND_ERROR_CODE = "EMAIL001"
    const val INVALID_REQUEST_ERROR_CODE = "REQ001"
    const val NO_AUTHORIZATION_ERROR_CODE = "AUTH003"
    const val PERMISSION_DENIED_ERROR_CODE = "AUTH004"
    const val RESOURCE_NOT_FOUND_ERROR_CODE = "RES001"
    const val TIMEOUT_ERROR_CODE = "TIME001"
    const val TOO_MANY_REQUESTS_ERROR_CODE = "REQ002"
    const val VALIDATION_ERROR_CODE = "VAL002"
    const val JSON_MESSAGE_READ_ERROR_CODE = "VAL003"
    const val UNEXPECTED_ERROR_CODE = "SYS001"
    const val STORAGE_ERROR_CODE = "STORAGE001"

    // Error Messages
    const val AUTHENTICATION_ERROR_MESSAGE = "Falha na autenticação"
    const val EMAIL_SEND_ERROR_MESSAGE = "Falha ao enviar email"
    const val INVALID_REQUEST_ERROR_MESSAGE = "Requisição inválida"
    const val NO_AUTHORIZATION_ERROR_MESSAGE = "Sem autorização"
    const val PERMISSION_DENIED_ERROR_MESSAGE = "Permissão negada"
    const val RESOURCE_NOT_FOUND_ERROR_MESSAGE = "Recurso não encontrado"
    const val TIMEOUT_ERROR_MESSAGE = "Tempo de requisição esgotado"
    const val TOO_MANY_REQUESTS_ERROR_MESSAGE = "Muitas requisições"
    const val VALIDATION_ERROR_MESSAGE = "Erro de validação"
    const val JSON_MESSAGE_READ_ERROR_MESSAGE = "Erro ao ler a mensagem JSON"
    const val UNEXPECTED_ERROR_MESSAGE = "Erro inesperado"
    const val STORAGE_ERROR_MESSAGE = "Erro ao realizar a operação de armazenamento"
}