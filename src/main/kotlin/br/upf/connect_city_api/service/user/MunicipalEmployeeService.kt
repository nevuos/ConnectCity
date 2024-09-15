package br.upf.connect_city_api.service.user

import br.upf.connect_city_api.dtos.employee.CreateMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.dtos.employee.MunicipalEmployeeDetailsDTO
import br.upf.connect_city_api.dtos.employee.UpdateMunicipalEmployeeRequestDTO
import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.AdminRepository
import br.upf.connect_city_api.repository.MunicipalEmployeeRepository
import br.upf.connect_city_api.repository.specifications.MunicipalEmployeeSpecifications
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.util.constants.employee.MunicipalEmployeeMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class MunicipalEmployeeService(
    private val municipalEmployeeRepository: MunicipalEmployeeRepository,
    private val adminRepository: AdminRepository,
    private val userManagementService: UserManagementService,
    private val tokenService: TokenService,
    private val modelMapper: ModelMapper
) {

    @CacheEvict(
        value = ["municipalEmployeeById"],
        key = "#request.getUserPrincipal().name",
        cacheManager = "searchCacheManager"
    )
    fun create(request: HttpServletRequest, createMunicipalEmployeeRequest: CreateMunicipalEmployeeRequestDTO): String {
        val user = tokenService.getUserFromRequest(request)

        validateMunicipalEmployeeData(createMunicipalEmployeeRequest.cpf, user)

        val municipalEmployee = modelMapper.map(createMunicipalEmployeeRequest, MunicipalEmployee::class.java).apply {
            this.user = user
        }

        municipalEmployeeRepository.save(municipalEmployee)

        if (user.userType != UserType.MUNICIPAL_EMPLOYEE) {
            userManagementService.update(
                id = user.id,
                newType = UserType.MUNICIPAL_EMPLOYEE.name
            )
        }

        return MunicipalEmployeeMessages.EMPLOYEE_CREATED_SUCCESSFULLY
    }

    private fun validateMunicipalEmployeeData(cpf: String, user: User) {
        if (municipalEmployeeRepository.findByCpf(cpf) != null) {
            throw InvalidRequestError(MunicipalEmployeeMessages.CPF_ALREADY_EXISTS)
        }

        if (municipalEmployeeRepository.findByUser(user) != null) {
            throw InvalidRequestError(MunicipalEmployeeMessages.USER_ALREADY_ASSOCIATED)
        }
    }

    @Cacheable(
        value = ["municipalEmployeeById"],
        key = "#request.getUserPrincipal().name",
        cacheManager = "searchCacheManager"
    )
    fun getDetails(request: HttpServletRequest): MunicipalEmployeeDetailsDTO {
        val user = tokenService.getUserFromRequest(request)

        val municipalEmployee = municipalEmployeeRepository.findByUser(user)
            ?: throw ResourceNotFoundError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND)

        return modelMapper.map(municipalEmployee, MunicipalEmployeeDetailsDTO::class.java)
    }

    @CacheEvict(
        value = ["municipalEmployeeById"],
        key = "#request.getUserPrincipal().name",
        cacheManager = "searchCacheManager"
    )
    fun update(request: HttpServletRequest, updateMunicipalEmployeeRequest: UpdateMunicipalEmployeeRequestDTO): String {
        val user = tokenService.getUserFromRequest(request)

        val municipalEmployee = municipalEmployeeRepository.findByUser(user)
            ?: throw ResourceNotFoundError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND)

        modelMapper.map(updateMunicipalEmployeeRequest, municipalEmployee)
        municipalEmployeeRepository.save(municipalEmployee)

        return MunicipalEmployeeMessages.EMPLOYEE_UPDATED_SUCCESSFULLY
    }

    @CacheEvict(value = ["municipalEmployeeById"], key = "#municipalEmployeeId", cacheManager = "searchCacheManager")
    fun approve(municipalEmployeeId: Long, request: HttpServletRequest): String {
        val user = tokenService.getUserFromRequest(request)

        val admin = adminRepository.findByUser(user)
            ?: throw ResourceNotFoundError(MunicipalEmployeeMessages.ADMIN_NOT_FOUND)

        val municipalEmployee = municipalEmployeeRepository.findById(municipalEmployeeId)
            .orElseThrow { ResourceNotFoundError(MunicipalEmployeeMessages.EMPLOYEE_NOT_FOUND) }

        if (municipalEmployee.isApproved) {
            throw InvalidRequestError(MunicipalEmployeeMessages.EMPLOYEE_ALREADY_APPROVED)
        }

        municipalEmployee.isApproved = true
        municipalEmployee.approvingAdmin = admin
        municipalEmployeeRepository.save(municipalEmployee)

        return MunicipalEmployeeMessages.EMPLOYEE_APPROVED_SUCCESSFULLY
    }

    @Cacheable(
        value = ["searchMunicipalEmployees"],
        key = "{#firstName, #lastName, #cpf, #jobTitle, #department, #pageable}",
        cacheManager = "searchCacheManager"
    )
    fun search(
        firstName: String? = null,
        lastName: String? = null,
        cpf: String? = null,
        jobTitle: String? = null,
        department: String? = null,
        pageable: Pageable
    ): Page<MunicipalEmployeeDetailsDTO> {
        val spec = Specification.where(MunicipalEmployeeSpecifications.firstNameContains(firstName))
            .and(MunicipalEmployeeSpecifications.lastNameContains(lastName))
            .and(MunicipalEmployeeSpecifications.cpfEquals(cpf))
            .and(MunicipalEmployeeSpecifications.jobTitleContains(jobTitle))
            .and(MunicipalEmployeeSpecifications.departmentContains(department))

        val employees = municipalEmployeeRepository.findAll(spec, pageable)
        return employees.map { employee -> modelMapper.map(employee, MunicipalEmployeeDetailsDTO::class.java) }
    }
}