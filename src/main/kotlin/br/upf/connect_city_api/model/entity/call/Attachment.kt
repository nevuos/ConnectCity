package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.Embeddable

@Embeddable
class Attachment(
    var fileName: String,
    var fileUrl: String,
    var version: Int = 1
)