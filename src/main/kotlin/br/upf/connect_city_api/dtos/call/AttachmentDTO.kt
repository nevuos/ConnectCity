package br.upf.connect_city_api.dtos.call

data class AttachmentDTO(
    val id: Long?,
    val fileName: String,
    val fileType: String,
    val fileUrl: String
)