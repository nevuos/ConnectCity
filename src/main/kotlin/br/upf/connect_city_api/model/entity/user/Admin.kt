package br.upf.connect_city_api.model.entity.user

import jakarta.persistence.*

@Entity
@Table(name = "admins")
data class Admin(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @OneToMany(mappedBy = "approvingAdmin", cascade = [CascadeType.ALL], orphanRemoval = true)
    var approvedEmployees: MutableList<MunicipalEmployee> = mutableListOf()
)