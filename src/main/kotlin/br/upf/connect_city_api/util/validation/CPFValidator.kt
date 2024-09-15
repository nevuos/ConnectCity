package br.upf.connect_city_api.util.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class CPFValidator : ConstraintValidator<ValidCPF, String> {

    override fun isValid(cpf: String?, context: ConstraintValidatorContext): Boolean {
        if (cpf.isNullOrEmpty()) {
            return true
        }

        val cleanedCPF = cpf.filter { it.isDigit() }
        if (cleanedCPF.length != 11 || cleanedCPF.all { it == cleanedCPF[0] }) {
            return false
        }

        val digits = cleanedCPF.map { Character.getNumericValue(it) }
        val firstVerifier = calculateVerifierDigit(digits.take(9))
        val secondVerifier = calculateVerifierDigit(digits.take(9) + firstVerifier)

        return digits[9] == firstVerifier && digits[10] == secondVerifier
    }

    private fun calculateVerifierDigit(digits: List<Int>): Int {
        val sum = digits.mapIndexed { index, digit -> digit * (digits.size + 1 - index) }.sum()
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }
}