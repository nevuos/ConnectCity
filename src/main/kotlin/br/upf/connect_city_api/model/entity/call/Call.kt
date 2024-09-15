package br.upf.connect_city_api.model.entity.call

import br.upf.connect_city_api.dtos.address.AddressDTO
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.model.entity.user.Citizen
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calls")
class Call(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = true)
    var citizen: Citizen? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = true)
    var employee: MunicipalEmployee? = null,

    @Embedded
    var address: AddressDTO,

    @Column(nullable = false)
    var subject: String,

    @Column(nullable = false, length = 1000)
    var description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CallStatus = CallStatus.OPEN,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: PriorityLevel = PriorityLevel.MEDIUM,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var closedAt: LocalDateTime? = null,

    @Column(nullable = true)
    var estimatedCompletion: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", nullable = true)
    var assignedTo: MunicipalEmployee? = null,  // Funcionário municipal responsável pelo chamado

    @OneToMany(mappedBy = "call", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var steps: MutableList<Step> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "call_tags", joinColumns = [JoinColumn(name = "call_id")])
    @Column(name = "tag")
    var tags: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "call_interactions", joinColumns = [JoinColumn(name = "call_id")])
    var interactionHistory: MutableList<Interaction> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "call_category_mapping",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    var categories: MutableList<Category> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "call_attachments", joinColumns = [JoinColumn(name = "call_id")])
    var attachments: MutableList<Attachment> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "status_history", joinColumns = [JoinColumn(name = "call_id")])
    var statusHistory: MutableList<StatusChange> = mutableListOf(),

    @Column(nullable = false)
    var createdBy: String,

    @Column(nullable = true)
    var updatedBy: String? = null,

    @Column(nullable = false)
    var isPublic: Boolean = true,

    @ElementCollection
    @CollectionTable(name = "call_visibility_groups", joinColumns = [JoinColumn(name = "call_id")])
    var visibleToGroups: MutableList<String> = mutableListOf(),

    @Column(nullable = true)
    var progressPercentage: Int = 0,

    @Column(nullable = true, length = 1000)
    var progressNotes: String? = null,

    @Column(nullable = true)
    var responseTime: Long? = null,

    @Column(nullable = true)
    var resolutionTime: Long? = null,

    @Column(nullable = true)
    var reopenedCount: Int = 0,

    @Column(nullable = true)
    var lastNotifiedAt: LocalDateTime? = null,

    @Column(nullable = false)
    var language: String = "pt-BR",

    @Column(nullable = true, length = 2000)
    var rootCauseAnalysis: String? = null,

    @Column(nullable = true)
    var escalationLevel: Int = 0,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "related_calls_predecessors",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "predecessor_call_id")]
    )
    var predecessorCalls: MutableList<Call> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "related_calls_successors",
        joinColumns = [JoinColumn(name = "call_id")],
        inverseJoinColumns = [JoinColumn(name = "successor_call_id")]
    )
    var successorCalls: MutableList<Call> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "notification_history", joinColumns = [JoinColumn(name = "call_id")])
    var notificationHistory: MutableList<Notification> = mutableListOf()
)