package br.upf.connect_city_api.dtos.call

import br.upf.connect_city_api.br.upf.connect_city_api.dtos.call.NotificationDTO
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import java.time.LocalDateTime

data class CallDetailsDTO(
    val id: Long?,
    val citizenId: Long?,
    val employeeId: Long?,
    val subject: String,
    val description: String,
    val status: CallStatus,
    val priority: PriorityLevel,
    val createdAt: LocalDateTime,
    val closedAt: LocalDateTime?,
    val estimatedCompletion: LocalDateTime?,
    val assignedToId: Long?,
    val steps: List<CreateStepRequestDTO>,
    val tags: List<String>,
    val interactionHistory: List<InteractionRequestDTO>,
    val categories: List<CategoryDTO>,
    val attachments: List<AttachmentDTO>,
    val statusHistory: List<StatusChangeDTO>,
    val createdBy: String,
    val updatedBy: String?,
    val isPublic: Boolean,
    val visibleToGroups: List<String>,
    val progressPercentage: Int?,
    val progressNotes: String?,
    val responseTime: Long?,
    val resolutionTime: Long?,
    val reopenedCount: Int?,
    val lastNotifiedAt: LocalDateTime?,
    val language: String = "pt-BR",
    val rootCauseAnalysis: String?,
    val escalationLevel: Int?,
    val predecessorCalls: List<CallDTO>,
    val successorCalls: List<CallDTO>,
    val notificationHistory: List<NotificationDTO>
)