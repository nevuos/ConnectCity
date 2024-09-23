package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var notificationType: String,

    @Column(nullable = false)
    var sentAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var status: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id", nullable = false)
    var call: Call
)