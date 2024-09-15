package br.upf.connect_city_api.service.user

import br.upf.connect_city_api.dtos.user.UpdateUserRequestDTO
import br.upf.connect_city_api.dtos.user.UserDetailsDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.UserRepository
import br.upf.connect_city_api.repository.specifications.UserSpecifications
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.util.constants.user.UserMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.modelmapper.ModelMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserManagementService(
    private val userRepository: UserRepository,
    private val modelMapper: ModelMapper,
    private val tokenService: TokenService,
    private val userValidationService: UserValidationService
) {

    @Cacheable(
        value = ["userDetails"],
        key = "#request.getHeader('Authorization')",
        cacheManager = "searchCacheManager"
    )
    fun getDetails(request: HttpServletRequest): UserDetailsDTO {
        val user = getUserFromRequest(request)
        return mapToUserDetailsDTO(user)
    }

    @CacheEvict(
        value = ["userByEmail", "userById", "userDetails", "searchUsers"],
        allEntries = true,
        cacheManager = "searchCacheManager"
    )
    fun update(
        id: Long? = null,
        request: HttpServletRequest? = null,
        updateRequest: UpdateUserRequestDTO? = null,
        newType: String? = null,
        isActive: Boolean? = null,
        response: HttpServletResponse? = null
    ): String {
        val user = getUserForUpdate(id, request)
        updateUserDetails(user, updateRequest, newType, isActive)
        if (isActive == false && id == null) {
            handleAccountDeactivation(response!!)
        }
        userRepository.save(user)
        return determineUpdateMessage(isActive)
    }

    @CacheEvict(
        value = ["userByEmail", "userById", "userDetails", "searchUsers"],
        allEntries = true,
        cacheManager = "searchCacheManager"
    )
    fun updateStatusByAdmin(
        id: Long,
        isActive: Boolean
    ): String {
        val user = findById(id)
        if (user.isActive == isActive) {
            return if (isActive) {
                UserMessages.USER_ALREADY_ACTIVE
            } else {
                UserMessages.USER_ALREADY_DISABLED
            }
        }
        updateActiveStatus(user, isActive)
        userRepository.save(user)
        return determineUpdateMessage(isActive)
    }

    @Cacheable(
        value = ["searchUsers"],
        key = "{#username, #email, #userType, #isActive, #createdAfter, #createdBefore, #createdOn, #pageable}",
        cacheManager = "searchCacheManager"
    )
    fun search(
        username: String?,
        email: String?,
        userType: String?,
        isActive: Boolean?,
        createdAfter: LocalDateTime?,
        createdBefore: LocalDateTime?,
        createdOn: LocalDateTime?,
        pageable: Pageable
    ): Page<UserDetailsDTO> {
        val spec = createUserSpecification(username, email, userType, isActive, createdAfter, createdBefore, createdOn)
        val usersPage = userRepository.findAll(spec, pageable)
        return usersPage.map { user -> mapToUserDetailsDTO(user) }
    }

    @Cacheable(value = ["userById"], key = "#userId", cacheManager = "searchCacheManager")
    internal fun findById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow {
            ResourceNotFoundError(UserMessages.USER_NOT_FOUND)
        }
    }

    @Cacheable(
        value = ["userFromToken"],
        key = "#request.getHeader('Authorization')",
        cacheManager = "searchCacheManager"
    )
    internal fun getUserFromRequest(request: HttpServletRequest): User {
        val userId = tokenService.getUserFromRequest(request).id
        return findById(userId)
    }

    private fun getUserForUpdate(id: Long?, request: HttpServletRequest?): User {
        return id?.let { findById(it) } ?: getUserFromRequest(request!!)
    }

    private fun updateUserDetails(
        user: User,
        updateRequest: UpdateUserRequestDTO?,
        newType: String?,
        isActive: Boolean?
    ) {
        updateRequest?.let {
            processUpdateRequest(user, it)
        }
        newType?.let {
            updateUserType(user, it)
        }
        isActive?.let {
            updateActiveStatus(user, it)
        }
    }

    private fun processUpdateRequest(user: User, updateRequest: UpdateUserRequestDTO) {
        updateRequest.password?.let { password ->
            user.password = userValidationService.encodePassword(password)
        }
        updateRequest.username?.let { username ->
            updateRequest.email?.let { email ->
                userValidationService.validateUserDetails(username, email, user.id)
            }
        }
        modelMapper.map(updateRequest.copy(password = null), user)
    }

    private fun updateUserType(user: User, newType: String) {
        val userType = UserType.valueOf(newType.uppercase())
        user.userType = userType
    }

    private fun updateActiveStatus(user: User, isActive: Boolean) {
        user.isActive = isActive
    }

    private fun determineUpdateMessage(isActive: Boolean?): String {
        return if (isActive == false) UserMessages.USER_ACCOUNT_DISABLED_SUCCESSFULLY else UserMessages.USER_UPDATED_SUCCESSFULLY
    }

    private fun createUserSpecification(
        username: String?,
        email: String?,
        userType: String?,
        isActive: Boolean?,
        createdAfter: LocalDateTime?,
        createdBefore: LocalDateTime?,
        createdOn: LocalDateTime?
    ): Specification<User> {
        val userTypeEnum = try {
            userType?.let { UserType.valueOf(it.uppercase()) }
        } catch (e: IllegalArgumentException) {
            throw InvalidRequestError(String.format(UserMessages.INVALID_USER_TYPE, userType))
        }

        return Specification.where(UserSpecifications.usernameContains(username))
            .and(UserSpecifications.emailContains(email))
            .and(UserSpecifications.userTypeEquals(userTypeEnum))
            .and(UserSpecifications.isActiveEquals(isActive))
            .and(UserSpecifications.createdAfter(createdAfter))
            .and(UserSpecifications.createdBefore(createdBefore))
            .and(UserSpecifications.createdOn(createdOn))
    }

    private fun mapToUserDetailsDTO(user: User): UserDetailsDTO {
        return modelMapper.map(user, UserDetailsDTO::class.java)
    }

    private fun handleAccountDeactivation(response: HttpServletResponse) {
        tokenService.clearTokensFromResponse(response)
    }
}