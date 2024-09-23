package br.upf.connect_city_api.service.call

import br.upf.connect_city_api.dtos.call.*
import br.upf.connect_city_api.model.entity.address.Address
import br.upf.connect_city_api.model.entity.call.*
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import br.upf.connect_city_api.repository.*
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.service.storage.StorageService
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.constants.storage.StorageConstants
import br.upf.connect_city_api.util.constants.user.UserMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class CallService(
    private val callRepository: CallRepository,
    private val citizenRepository: CitizenRepository,
    private val municipalEmployeeRepository: MunicipalEmployeeRepository,
    private val categoryRepository: CategoryRepository,
    private val storageService: StorageService,
    private val tokenService: TokenService,
    private val modelMapper: ModelMapper
) {

    @Transactional
    fun create(request: HttpServletRequest, createRequest: CreateCallRequestDTO, attachments: List<MultipartFile>?): String {
        val user = tokenService.getUserFromRequest(request)

        val (citizen, employee) = getUserProfile(user.id)

        val categories = createRequest.categoryIds?.let {
            categoryRepository.findAllById(it).toMutableList()
        } ?: mutableListOf()

        val addressEntity = modelMapper.map(createRequest.address, Address::class.java)

        val phoneNumber = createRequest.phoneNumber ?: citizen?.phoneNumber

        val createdBy = getProfileName(citizen, employee)

        val call = Call(
            citizen = citizen,
            employee = employee,
            address = addressEntity,
            subject = createRequest.subject,
            description = createRequest.description,
            status = CallStatus.OPEN,
            priority = createRequest.priority,
            createdBy = createdBy,
            phoneNumber = phoneNumber,
            isPublic = createRequest.isPublic ?: true,
            language = createRequest.language ?: "pt-BR",
            createdAt = LocalDateTime.now(),
            categories = categories
        )

        callRepository.save(call)

        attachments?.let {
            val uploadFutures = attachments.map { file ->
                storageService.uploadFileAsync(file).thenApply { storedFilePath ->
                    val attachment = Attachment(
                        call = call,
                        fileName = file.originalFilename ?: StorageConstants.DEFAULT_FILE_NAME,
                        fileUrl = storedFilePath,
                        version = 1
                    )
                    call.attachments.add(attachment)
                }
            }

            CompletableFuture.allOf(*uploadFutures.toTypedArray()).thenRun {
                callRepository.save(call)
            }
        }

        return CallMessages.CALL_CREATED_SUCCESSFULLY
    }

    @Transactional
    fun createCategory(createRequest: CreateCategoryRequestDTO): String {
        if (categoryRepository.existsByName(createRequest.name)) {
            throw InvalidRequestError(CallMessages.CATEGORY_ALREADY_EXISTS)
        }

        val category = Category(name = createRequest.name)
        categoryRepository.save(category)

        return CallMessages.CATEGORY_CREATED_SUCCESS
    }

    private fun getUserProfile(userId: Long): Pair<Citizen?, MunicipalEmployee?> {
        val citizen = citizenRepository.findById(userId).orElse(null)
        val employee = municipalEmployeeRepository.findById(userId).orElse(null)

        if (citizen == null && employee == null) {
            throw ResourceNotFoundError("${UserMessages.PROFILE_NOT_FOUND}$userId")
        }

        return citizen to employee
    }

    private fun getProfileName(citizen: Citizen?, employee: MunicipalEmployee?): String {
        return when {
            citizen != null -> "${citizen.firstName} ${citizen.lastName}"
            employee != null -> "${employee.firstName} ${employee.lastName}"
            else -> throw ResourceNotFoundError(UserMessages.PROFILE_NOT_FOUND)
        }
    }
}