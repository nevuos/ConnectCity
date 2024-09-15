package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
class Notification(
    var notificationType: String,
    var sentAt: LocalDateTime = LocalDateTime.now(),
    var status: String
)