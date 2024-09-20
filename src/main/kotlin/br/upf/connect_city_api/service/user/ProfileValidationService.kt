package br.upf.connect_city_api.service.user

import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.CitizenRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import br.upf.connect_city_api.util.constants.profile.ProfileMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import org.springframework.stereotype.Service

@Service
class ProfileValidationService(
    private val citizenRepository: CitizenRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository
) {

    fun validateProfileData(cpf: String?, phoneNumber: String?, user: User, existingProfileId: Long? = null) {
        val validationErrors = mutableListOf<String>()

        cpf?.takeIf { isCpfTaken(it, existingProfileId) }?.let {
            validationErrors.add(ProfileMessages.CPF_ALREADY_EXISTS)
        }

        phoneNumber?.takeIf { isPhoneNumberTaken(it, existingProfileId) }?.let {
            validationErrors.add(ProfileMessages.PHONE_NUMBER_ALREADY_EXISTS)
        }

        if (isUserAssociated(user, existingProfileId)) {
            validationErrors.add(ProfileMessages.USER_ALREADY_ASSOCIATED)
        }

        validationErrors.takeIf { it.isNotEmpty() }?.let {
            throw InvalidRequestError(it.joinToString(""))
        }
    }

    private fun isCpfTaken(cpf: String, existingProfileId: Long?): Boolean {
        return (citizenRepository.findByCpf(cpf)?.id != existingProfileId)
                || (municipalEmployeeRepository.findByCpf(cpf)?.id != existingProfileId)
    }

    private fun isPhoneNumberTaken(phoneNumber: String, existingProfileId: Long?): Boolean {
        return (citizenRepository.findByPhoneNumber(phoneNumber)?.id != existingProfileId)
                || (municipalEmployeeRepository.findByPhoneNumber(phoneNumber)?.id != existingProfileId)
    }

    private fun isUserAssociated(user: User, existingProfileId: Long?): Boolean {
        return (citizenRepository.findByUser(user)?.id != existingProfileId)
                || (municipalEmployeeRepository.findByUser(user)?.id != existingProfileId)
    }
}