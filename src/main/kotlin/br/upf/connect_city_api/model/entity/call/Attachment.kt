package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "attachments")
class Attachment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var fileName: String,

    @Column(nullable = false)
    var fileUrl: String,

    @Column(nullable = false)
    var version: Int = 1,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id", nullable = true)
    var call: Call? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = true)
    var step: Step? = null
)