package br.upf.connect_city_api.service.call.update.strategy

import br.upf.connect_city_api.dtos.call.UpdateCallByCitizenCreatorRequestDTO
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.service.storage.AttachmentService
import br.upf.connect_city_api.util.constants.call.CallMessages
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Component
class CitizenUpdateStrategy(
    private val modelMapper: ModelMapper,
    private val callRepository: CallRepository,
    private val attachmentService: AttachmentService
) : UpdateStrategy<UpdateCallByCitizenCreatorRequestDTO> {

    override fun updateCall(
        user: User,
        call: Call,
        updateRequest: UpdateCallByCitizenCreatorRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        modelMapper.map(updateRequest, call)
        attachmentService.processAttachmentsForCall(call.id!!, attachments, removeAttachmentIds)
        call.updatedAt = LocalDateTime.now()
        callRepository.save(call)
        return CallMessages.CALL_UPDATED_SUCCESSFULLY
    }
}