package br.upf.connect_city_api.service.call.update.strategy

import br.upf.connect_city_api.dtos.call.UpdateCallByMunicipalEmployeeCreatorRequestDTO
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.service.storage.AttachmentService
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.exception.PermissionDeniedError
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Component
class EmployeeUpdateStrategy(
    private val modelMapper: ModelMapper,
    private val callRepository: CallRepository,
    private val attachmentService: AttachmentService
) : UpdateStrategy<UpdateCallByMunicipalEmployeeCreatorRequestDTO> {

    override fun updateCall(
        user: User,
        call: Call,
        updateRequest: UpdateCallByMunicipalEmployeeCreatorRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        if (updateRequest.assignedToEmployeeId != null) {
            throw PermissionDeniedError(CallMessages.ONLY_MANAGER_CAN_ASSIGN)
        }

        modelMapper.map(updateRequest, call)
        attachmentService.processAttachmentsForCall(call.id!!, attachments, removeAttachmentIds)
        call.updatedAt = LocalDateTime.now()
        callRepository.save(call)
        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }
}