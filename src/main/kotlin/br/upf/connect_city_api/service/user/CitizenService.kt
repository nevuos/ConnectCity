package br.upf.connect_city_api.service.user

import br.upf.connect_city_api.dtos.citizen.CitizenDetailsDTO
import br.upf.connect_city_api.dtos.citizen.CreateCitizenRequestDTO
import br.upf.connect_city_api.dtos.citizen.UpdateCitizenRequestDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.repository.CitizenRepository
import br.upf.connect_city_api.repository.specifications.CitizenSpecifications
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.util.constants.citizen.CitizenMessages
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CitizenService(
    private val citizenRepository: CitizenRepository,
    private val userManagementService: UserManagementService,
    private val profileValidationService: ProfileValidationService,
    private val tokenService: TokenService,
    private val modelMapper: ModelMapper
) {

    @CacheEvict(value = ["citizenById"], key = "#request.getUserPrincipal().name", cacheManager = "searchCacheManager")
    fun create(request: HttpServletRequest, createCitizenRequest: CreateCitizenRequestDTO): String {
        val user = tokenService.getUserFromRequest(request)

        profileValidationService.validateProfileData(createCitizenRequest.cpf, createCitizenRequest.phoneNumber, user)

        val citizen = modelMapper.map(createCitizenRequest, Citizen::class.java).apply {
            this.user = user
        }

        citizenRepository.save(citizen)

        if (user.userType != UserType.CITIZEN) {
            userManagementService.update(
                id = user.id,
                newType = UserType.CITIZEN.name
            )
        }

        return CitizenMessages.CITIZEN_CREATED_SUCCESSFULLY
    }

    @Cacheable(value = ["citizenById"], key = "#request.getUserPrincipal().name", cacheManager = "searchCacheManager")
    fun getDetails(request: HttpServletRequest): CitizenDetailsDTO {
        val user = tokenService.getUserFromRequest(request)

        val citizen = citizenRepository.findByUser(user)
            ?: throw ResourceNotFoundError(CitizenMessages.CITIZEN_NOT_FOUND)
        return modelMapper.map(citizen, CitizenDetailsDTO::class.java)
    }

    @CacheEvict(value = ["citizenById"], key = "#request.getUserPrincipal().name", cacheManager = "searchCacheManager")
    fun update(request: HttpServletRequest, updateCitizenRequest: UpdateCitizenRequestDTO): String {
        val user = tokenService.getUserFromRequest(request)
        val citizen = citizenRepository.findByUser(user)
            ?: throw ResourceNotFoundError(CitizenMessages.CITIZEN_NOT_FOUND)

        profileValidationService.validateProfileData(
            updateCitizenRequest.cpf,
            updateCitizenRequest.phoneNumber,
            user,
            citizen.id
        )

        modelMapper.map(updateCitizenRequest, citizen)
        citizenRepository.save(citizen)
        return CitizenMessages.CITIZEN_UPDATED_SUCCESSFULLY
    }

    @Cacheable(
        value = ["searchCitizens"],
        key = "{#firstName, #lastName, #cpf, #dateOfBirth, #gender, #phoneNumber, #pageable}",
        cacheManager = "searchCacheManager"
    )
    fun search(
        firstName: String? = null,
        lastName: String? = null,
        cpf: String? = null,
        dateOfBirth: LocalDate? = null,
        gender: String? = null,
        phoneNumber: String? = null,
        pageable: Pageable
    ): Page<CitizenDetailsDTO> {
        val spec = Specification.where(CitizenSpecifications.firstNameContains(firstName))
            .and(CitizenSpecifications.lastNameContains(lastName))
            .and(CitizenSpecifications.cpfEquals(cpf))
            .and(CitizenSpecifications.dateOfBirthEquals(dateOfBirth))
            .and(CitizenSpecifications.genderEquals(gender))
            .and(CitizenSpecifications.phoneNumberContains(phoneNumber))

        val citizens = citizenRepository.findAll(spec, pageable)
        return citizens.map { citizen -> modelMapper.map(citizen, CitizenDetailsDTO::class.java) }
    }
}