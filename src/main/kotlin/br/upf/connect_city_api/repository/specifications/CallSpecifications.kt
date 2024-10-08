package br.upf.connect_city_api.repository.specifications

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.call.Category
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime

object CallSpecifications {

    fun hasSubject(subject: String?): Specification<Call>? {
        return subject?.takeIf { it.isNotBlank() }?.let { searchTerm ->
            Specification { root, _, cb ->
                cb.like(
                    cb.lower(root.get("subject")),
                    "%${searchTerm.lowercase()}%"
                )
            }
        }
    }

    fun hasDescription(description: String?): Specification<Call>? {
        return description?.takeIf { it.isNotBlank() }?.let { searchTerm ->
            Specification { root, _, cb ->
                cb.like(
                    cb.lower(root.get("description")),
                    "%${searchTerm.lowercase()}%"
                )
            }
        }
    }

    fun citizenNameContains(name: String?): Specification<Call>? {
        return name?.takeIf { it.isNotBlank() }?.let { searchTerm ->
            Specification { root, _, cb ->
                val citizenJoin = root.join<Call, String>("citizen", JoinType.LEFT)
                val firstNameLower = cb.lower(citizenJoin.get<String>("firstName"))
                val lastNameLower = cb.lower(citizenJoin.get<String>("lastName"))
                val fullNameLower = cb.concat(cb.concat(firstNameLower, " "), lastNameLower)
                val searchTermLower = "%${searchTerm.lowercase()}%"
                cb.or(
                    cb.like(firstNameLower, searchTermLower),
                    cb.like(lastNameLower, searchTermLower),
                    cb.like(fullNameLower, searchTermLower)
                )
            }
        }
    }

    fun employeeNameContains(name: String?): Specification<Call>? {
        return name?.takeIf { it.isNotBlank() }?.let { searchTerm ->
            Specification { root, _, cb ->
                val employeeJoin = root.join<Call, String>("assignedTo", JoinType.LEFT)
                val firstNameLower = cb.lower(employeeJoin.get<String>("firstName"))
                val lastNameLower = cb.lower(employeeJoin.get<String>("lastName"))
                val fullNameLower = cb.concat(cb.concat(firstNameLower, " "), lastNameLower)
                val searchTermLower = "%${searchTerm.lowercase()}%"
                cb.or(
                    cb.like(firstNameLower, searchTermLower),
                    cb.like(lastNameLower, searchTermLower),
                    cb.like(fullNameLower, searchTermLower)
                )
            }
        }
    }

    fun hasStatuses(statuses: List<CallStatus>?): Specification<Call>? {
        return statuses?.takeIf { it.isNotEmpty() }?.let {
            Specification { root, _, _ ->
                root.get<CallStatus>("status").`in`(it)
            }
        }
    }

    fun hasPriorities(priorities: List<PriorityLevel>?): Specification<Call>? {
        return priorities?.takeIf { it.isNotEmpty() }?.let {
            Specification { root, _, _ ->
                root.get<PriorityLevel>("priority").`in`(it)
            }
        }
    }

    fun createdAtBetween(start: LocalDateTime?, end: LocalDateTime?): Specification<Call>? {
        return when {
            start != null && end != null -> Specification { root, _, cb ->
                cb.between(root.get("createdAt"), start, end)
            }

            start != null -> Specification { root, _, cb ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), start)
            }

            end != null -> Specification { root, _, cb ->
                cb.lessThanOrEqualTo(root.get("createdAt"), end)
            }

            else -> null
        }
    }

    fun closedAtBetween(start: LocalDateTime?, end: LocalDateTime?): Specification<Call>? {
        return when {
            start != null && end != null -> Specification { root, _, cb ->
                cb.between(root.get("closedAt"), start, end)
            }

            start != null -> Specification { root, _, cb ->
                cb.greaterThanOrEqualTo(root.get("closedAt"), start)
            }

            end != null -> Specification { root, _, cb ->
                cb.lessThanOrEqualTo(root.get("closedAt"), end)
            }

            else -> null
        }
    }

    fun estimatedCompletionBetween(start: LocalDateTime?, end: LocalDateTime?): Specification<Call>? {
        return when {
            start != null && end != null -> Specification { root, _, cb ->
                cb.between(root.get("estimatedCompletion"), start, end)
            }

            start != null -> Specification { root, _, cb ->
                cb.greaterThanOrEqualTo(root.get("estimatedCompletion"), start)
            }

            end != null -> Specification { root, _, cb ->
                cb.lessThanOrEqualTo(root.get("estimatedCompletion"), end)
            }

            else -> null
        }
    }

    fun hasCategories(categoryIds: List<Long>?): Specification<Call>? {
        return categoryIds?.takeIf { it.isNotEmpty() }?.let {
            Specification { root, _, _ ->
                val categoriesJoin = root.join<Call, Category>("categories", JoinType.LEFT)
                categoriesJoin.get<Long>("id").`in`(it)
            }
        }
    }
}