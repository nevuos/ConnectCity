package br.upf.connect_city_api.service.user

import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.UserRepository
import br.upf.connect_city_api.util.constants.user.UserMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAuthenticationService(
    private val userRepository: UserRepository,
    private val userValidationService: UserValidationService
) {

    @Transactional
    fun register(username: String, password: String, email: String, userType: UserType = UserType.TEMPORARY) {
        userValidationService.validateUserDetails(username, email)
        val encodedPassword = userValidationService.encodePassword(password)
        val newUser = User(
            username = username,
            password = encodedPassword,
            email = email,
            userType = userType
        )
        userRepository.save(newUser)
    }

    @Transactional
    fun updatePassword(id: Long, newPassword: String) {
        val user = findById(id)
        user.password = userValidationService.encodePassword(newPassword)
        userRepository.save(user)
    }

    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw ResourceNotFoundError(UserMessages.USER_NOT_FOUND)
    }

    @Transactional
    fun confirmEmail(email: String, emailConfirmed: Boolean) {
        val user = findByEmail(email)
        if (user.emailConfirmed && emailConfirmed) {
            throw InvalidRequestError(UserMessages.EMAIL_ALREADY_CONFIRMED)
        }
        user.emailConfirmed = emailConfirmed
        userRepository.save(user)
    }

    fun isEmailConfirmed(userId: Long): Boolean {
        return findById(userId).emailConfirmed
    }

    fun verifyForResendConfirmation(email: String) {
        val user = userRepository.findByEmail(email)
        if (user == null || user.emailConfirmed) {
            throw InvalidRequestError(UserMessages.EMAIL_ALREADY_CONFIRMED_OR_NOT_FOUND)
        }
    }

    internal fun findById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow {
            ResourceNotFoundError(UserMessages.USER_NOT_FOUND)
        }
    }
}