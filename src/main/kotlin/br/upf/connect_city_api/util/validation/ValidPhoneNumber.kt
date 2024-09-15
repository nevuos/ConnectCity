package br.upf.connect_city_api.util.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [PhoneNumberValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidPhoneNumber(
    val message: String = "O número de telefone deve estar no formato +<código do país> <código da área> <número>. Ex: +55 11 99999-9999",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)