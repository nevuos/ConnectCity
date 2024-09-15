package br.upf.connect_city_api.repository


import br.upf.connect_city_api.model.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u WHERE u.createdAt <= :thresholdDate AND u.emailConfirmed = false")
    fun findExpiredUsers(@Param("thresholdDate") thresholdDate: LocalDateTime): List<User>
}