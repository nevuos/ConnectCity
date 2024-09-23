package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "interactions")
class Interaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id", nullable = false)
    var call: Call,

    @Column(nullable = false)
    var date: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedBy: String,

    @Column(nullable = false, length = 1000)
    var updateDetails: String
)