package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.model.entity.enums.CallStatus

data class StepDTO(
    var id: Long?,
    var callId: Long?,
    val description: String,
    val status: CallStatus,
    val assignedTo: Long?
)