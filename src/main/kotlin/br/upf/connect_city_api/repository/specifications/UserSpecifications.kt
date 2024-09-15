package br.upf.connect_city_api.repository.specifications

import br.upf.connect_city_api.model.entity.enums.UserType
import br.upf.connect_city_api.model.entity.user.User
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime
import java.util.*

object UserSpecifications {

    fun usernameContains(username: String?): Specification<User>? {
        return username?.let {
            Specification { root, _, cb -> cb.like(cb.lower(root.get("username")), "%${it.lowercase()}%") }
        }
    }

    fun emailContains(email: String?): Specification<User>? {
        return email?.let {
            Specification { root, _, cb -> cb.like(cb.lower(root.get("email")), "%${it.lowercase()}%") }
        }
    }

    fun userTypeEquals(userType: UserType?): Specification<User>? {
        return userType?.let {
            Specification { root, _, cb -> cb.equal(root.get<UserType>("userType"), it) }
        }
    }

    fun isActiveEquals(isActive: Boolean?): Specification<User>? {
        return isActive?.let {
            Specification { root, _, cb -> cb.equal(root.get<Boolean>("isActive"), it) }
        }
    }

    fun createdAfter(createdAfter: LocalDateTime?): Specification<User>? {
        return createdAfter?.let {
            Specification { root, _, cb -> cb.greaterThanOrEqualTo(root.get("createdAt"), it) }
        }
    }

    fun createdBefore(createdBefore: LocalDateTime?): Specification<User>? {
        return createdBefore?.let {
            Specification { root, _, cb -> cb.lessThanOrEqualTo(root.get("createdAt"), it) }
        }
    }

    fun createdOn(createdOn: LocalDateTime?): Specification<User>? {
        return createdOn?.let {
            Specification { root, _, cb ->
                val createdDate = cb.function("DATE", java.sql.Date::class.java, root.get<Date>("createdAt"))
                val targetDate = java.sql.Date.valueOf(it.toLocalDate())
                cb.equal(createdDate, targetDate)
            }
        }
    }

}
