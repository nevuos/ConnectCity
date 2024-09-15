package br.upf.connect_city_api.util.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PhoneNumberValidator : ConstraintValidator<ValidPhoneNumber, String> {

    private val phonePattern = "^\\+\\d{1,3}\\s?\\d{1,4}\\s?\\d{4,5}-\\d{4}\$".toRegex()

    override fun isValid(phoneNumber: String?, context: ConstraintValidatorContext): Boolean {
        if (phoneNumber.isNullOrBlank()) {
            return true
        }
        val normalizedPhoneNumber = if (!phoneNumber.startsWith("+")) {
            "+$phoneNumber"
        } else {
            phoneNumber
        }
        val isValid = phonePattern.matches(normalizedPhoneNumber)
        return isValid
    }
}