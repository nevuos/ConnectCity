package br.upf.connect_city_api.model.entity.user

import br.upf.connect_city_api.model.entity.address.Address
import br.upf.connect_city_api.model.entity.call.Call
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "citizens")
data class Citizen(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false, unique = true)
    var cpf: String,

    @Column(nullable = true)
    var dateOfBirth: LocalDate? = null,

    @Column(nullable = true)
    var gender: String? = null,

    @Column(nullable = true)
    var phoneNumber: String? = null,

    @Embedded
    var address: Address? = null,

    @OneToMany(mappedBy = "citizen", cascade = [CascadeType.ALL], orphanRemoval = true)
    var calls: MutableList<Call> = mutableListOf()
)