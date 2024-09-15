package br.upf.connect_city_api.model.error


import java.time.LocalDateTime

data class ErrorResponse(
    val errorCode: String,
    val errorMessage: String,
    val errorType: String,
    val details: List<String?>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)