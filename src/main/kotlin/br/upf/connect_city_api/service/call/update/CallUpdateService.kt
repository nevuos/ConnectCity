package br.upf.connect_city_api.service.call.update

import br.upf.connect_city_api.dtos.call.UpdateCallByCitizenCreatorRequestDTO
import br.upf.connect_city_api.dtos.call.UpdateCallByManagerRequestDTO
import br.upf.connect_city_api.dtos.call.UpdateCallByMunicipalEmployeeCreatorRequestDTO
import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.user.User
import br.upf.connect_city_api.service.call.update.strategy.CitizenUpdateStrategy
import br.upf.connect_city_api.service.call.update.strategy.EmployeeUpdateStrategy
import br.upf.connect_city_api.service.call.update.strategy.ManagerUpdateStrategy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class CallUpdateService(
    private val citizenUpdateStrategy: CitizenUpdateStrategy,
    private val employeeUpdateStrategy: EmployeeUpdateStrategy,
    private val managerUpdateStrategy: ManagerUpdateStrategy
) {

    @Transactional
    fun updateByCitizenCreator(
        user: User,
        call: Call,
        updateRequest: UpdateCallByCitizenCreatorRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        return citizenUpdateStrategy.updateCall(user, call, updateRequest, attachments, removeAttachmentIds)
    }

    @Transactional
    fun updateByMunicipalEmployeeCreator(
        user: User,
        call: Call,
        updateRequest: UpdateCallByMunicipalEmployeeCreatorRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        return employeeUpdateStrategy.updateCall(user, call, updateRequest, attachments, removeAttachmentIds)
    }

    @Transactional
    fun updateByManager(
        user: User,
        call: Call,
        updateRequest: UpdateCallByManagerRequestDTO,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String {
        return managerUpdateStrategy.updateCall(user, call, updateRequest, attachments, removeAttachmentIds)
    }
}