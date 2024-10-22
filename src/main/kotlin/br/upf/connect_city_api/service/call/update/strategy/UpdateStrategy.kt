package br.upf.connect_city_api.service.call.update.strategy

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.user.User
import org.springframework.web.multipart.MultipartFile

interface UpdateStrategy<T> {
    fun updateCall(
        user: User,
        call: Call,
        updateRequest: T,
        attachments: List<MultipartFile>?,
        removeAttachmentIds: List<Long>?
    ): String
}