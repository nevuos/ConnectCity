package br.upf.connect_city_api.model.entity.call

import br.upf.connect_city_api.model.entity.enums.CallStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "status_changes")
class StatusChange(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CallStatus,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val previousStatus: CallStatus?,

    @Column(nullable = false)
    var changedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var changedBy: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id", nullable = false)
    var call: Call
)