package br.upf.connect_city_api.util.constants.auth

class AuthUrls {
    companion object {
        private const val BASE_URL = "https://localhost:8443/connect-city/api/v1/auth/"
        const val CONFIRMATION_URL = BASE_URL + "confirm-email/"
        const val PASSWORD_RESET_URL = BASE_URL + "reset-password/"
    }
}