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
        cpf?.let {
            if (isCpfTaken(it, existingProfileId)) {
                validationErrors.add(ProfileMessages.CPF_ALREADY_EXISTS)
            }
        }
        phoneNumber?.let {
            if (isPhoneNumberTaken(it, existingProfileId)) {
                validationErrors.add(ProfileMessages.PHONE_NUMBER_ALREADY_EXISTS)
            }
        }
        if (isUserAssociated(user, existingProfileId)) {
            validationErrors.add(ProfileMessages.USER_ALREADY_ASSOCIATED)
        }
        if (validationErrors.isNotEmpty()) {
            throw InvalidRequestError(validationErrors.joinToString(" "))
        }
    }

    private fun isCpfTaken(cpf: String, existingProfileId: Long?): Boolean {
        val citizen = citizenRepository.findByCpf(cpf)
        val employee = municipalEmployeeRepository.findByCpf(cpf)

        return (citizen != null && citizen.id != existingProfileId) ||
                (employee != null && employee.id != existingProfileId)
    }

    private fun isPhoneNumberTaken(phoneNumber: String, existingProfileId: Long?): Boolean {
        val citizen = citizenRepository.findByPhoneNumber(phoneNumber)
        val employee = municipalEmployeeRepository.findByPhoneNumber(phoneNumber)

        return (citizen != null && citizen.id != existingProfileId) ||
                (employee != null && employee.id != existingProfileId)
    }

    private fun isUserAssociated(user: User, existingProfileId: Long?): Boolean {
        val citizen = citizenRepository.findByUser(user)
        val employee = municipalEmployeeRepository.findByUser(user)

        return (citizen != null && citizen.id != existingProfileId) ||
                (employee != null && employee.id != existingProfileId)
    }
}