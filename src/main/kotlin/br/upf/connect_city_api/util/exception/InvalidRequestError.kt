package br.upf.connect_city_api.util.exception

class InvalidRequestError(override val message: String?) : RuntimeException(message)
