package br.upf.connect_city_api.service.user

import br.upf.connect_city_api.repository.UserRepository
import br.upf.connect_city_api.util.constants.user.UserMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserValidationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun validateUserDetails(username: String, email: String, currentUserId: Long? = null) {
        val errors = mutableListOf<String>()

        if (isUsernameTaken(username, currentUserId)) {
            errors.add(UserMessages.USERNAME_ALREADY_EXISTS)
        }

        if (isEmailTaken(email, currentUserId)) {
            errors.add(UserMessages.EMAIL_ALREADY_EXIST)
        }

        if (errors.isNotEmpty()) {
            throw InvalidRequestError(errors.joinToString(" e "))
        }
    }

    fun isUsernameTaken(username: String, currentUserId: Long?): Boolean {
        val existingUserByUsername = userRepository.findByUsername(username)
        return existingUserByUsername != null && existingUserByUsername.id != currentUserId
    }

    fun isEmailTaken(email: String, currentUserId: Long?): Boolean {
        val existingUserByEmail = userRepository.findByEmail(email)
        return existingUserByEmail != null && existingUserByEmail.id != currentUserId
    }

    fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun validatePasswordMatches(encodedPassword: String, rawPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}