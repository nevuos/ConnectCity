package br.upf.connect_city_api.util.security

import br.upf.connect_city_api.model.entity.enums.UserType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserTypeRequired(vararg val value: UserType)