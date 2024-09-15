package br.upf.connect_city_api.util.exception

class TimeoutError(override val message: String?) : RuntimeException(message)