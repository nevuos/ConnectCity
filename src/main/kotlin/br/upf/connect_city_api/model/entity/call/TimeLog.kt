package br.upf.connect_city_api.model.entity.call

import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "time_logs")
class TimeLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    var employee: MunicipalEmployee,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    var step: Step,

    @Column(nullable = false)
    var timeSpentMinutes: Int = 0,

    @Column(nullable = false)
    var logDateTime: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true, length = 1000)
    var description: String? = null
)