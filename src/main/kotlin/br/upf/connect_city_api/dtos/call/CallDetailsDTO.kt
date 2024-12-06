package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.dtos.address.SimpleAddressDTO
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import java.time.LocalDateTime

data class CallDetailsDTO(
    val id: Long? = null,
    val citizenId: Long? = null,
    val employeeId: Long? = null,
    val subject: String? = null,
    val description: String? = null,
    val status: CallStatus? = null,
    val priority: PriorityLevel? = null,
    val createdAt: LocalDateTime? = null,
    val closedAt: LocalDateTime? = null,
    val estimatedCompletion: LocalDateTime? = null,
    val assignedToId: Long? = null,
    val steps: List<CreateStepRequestDTO>? = null,
    val tags: List<String>? = null,
    val interactionHistory: List<InteractionRequestDTO>? = null,
    val categories: List<CategoryDTO>? = null,
    val attachments: List<AttachmentDTO>? = null,
    val statusHistory: List<StatusChangeDTO>? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
    val isPublic: Boolean? = null,
    val visibleToGroups: List<String>? = null,
    val progressPercentage: Int? = null,
    val progressNotes: String? = null,
    val responseTime: Long? = null,
    val resolutionTime: Long? = null,
    val reopenedCount: Int? = null,
    val lastNotifiedAt: LocalDateTime? = null,
    val language: String? = "pt-BR",
    val rootCauseAnalysis: String? = null,
    val escalationLevel: Int? = null,
    val predecessorCalls: List<CallDTO>? = null,
    val successorCalls: List<CallDTO>? = null,
    var notificationHistory: List<NotificationDTO>? = null,
    val address: SimpleAddressDTO? = null,
    val phoneNumber: String? = null,
    val updatedAt: LocalDateTime? = null
)