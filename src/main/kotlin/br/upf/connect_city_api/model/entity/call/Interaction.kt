package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
class Interaction(
    var date: LocalDateTime = LocalDateTime.now(),
    var updatedBy: String,
    var updateDetails: String
)