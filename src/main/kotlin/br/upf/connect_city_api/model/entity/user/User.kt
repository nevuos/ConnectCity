package br.upf.connect_city_api.model.entity.user

import br.upf.connect_city_api.model.entity.enums.UserType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false)
    var username: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var userType: UserType,

    @Column(nullable = false)
    var emailConfirmed: Boolean = false,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)