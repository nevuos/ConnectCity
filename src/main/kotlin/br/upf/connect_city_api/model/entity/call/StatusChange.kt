package br.upf.connect_city_api.model.entity.call

import br.upf.connect_city_api.model.entity.enums.CallStatus
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
class StatusChange(
    var status: CallStatus,
    var changedAt: LocalDateTime = LocalDateTime.now(),
    var changedBy: String
)