package br.upf.connect_city_api.model.entity.user

import br.upf.connect_city_api.model.entity.call.TimeLog
import br.upf.connect_city_api.model.entity.enums.EmployeeType
import jakarta.persistence.*


@Entity
@Table(name = "municipal_employees")
class MunicipalEmployee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(nullable = false, unique = true)
    var cpf: String,

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,

    @Column(nullable = false)
    var jobTitle: String,

    @Column(nullable = false)
    var department: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var employeeType: EmployeeType = EmployeeType.INTERNAL,

    @Column(nullable = false)
    var isApproved: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approving_admin_id")
    var approvingAdmin: Admin? = null,

    @OneToMany(mappedBy = "employee", cascade = [CascadeType.ALL], orphanRemoval = true)
    var timeLogs: MutableList<TimeLog> = mutableListOf()
)