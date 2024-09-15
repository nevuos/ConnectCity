package br.upf.connect_city_api.model.entity.call

import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "steps")
class Step(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id", nullable = false)
    var call: Call,

    @Column(nullable = false)
    var description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: PriorityLevel = PriorityLevel.MEDIUM,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", nullable = true)
    var assignedTo: MunicipalEmployee? = null,

    @Column(nullable = false)
    var startTime: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var endTime: LocalDateTime? = null,

    @Column(nullable = true, length = 1000)
    var notes: String? = null,

    @ElementCollection
    @CollectionTable(name = "step_attachments", joinColumns = [JoinColumn(name = "step_id")])
    var attachments: MutableList<Attachment> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "time_logs", joinColumns = [JoinColumn(name = "step_id")])
    var timeLogs: MutableList<TimeLog> = mutableListOf()
)