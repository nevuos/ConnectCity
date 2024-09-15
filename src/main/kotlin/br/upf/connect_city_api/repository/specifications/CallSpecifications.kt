package br.upf.connect_city_api.repository.specifications

import br.upf.connect_city_api.model.entity.call.Call
import br.upf.connect_city_api.model.entity.enums.CallStatus
import br.upf.connect_city_api.model.entity.enums.PriorityLevel
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime


object CallSpecifications {

    fun subjectContains(subject: String?): Specification<Call>? {
        return subject?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("subject")), "%${it.lowercase()}%")
            }
        }
    }

    fun descriptionContains(description: String?): Specification<Call>? {
        return description?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%${it.lowercase()}%")
            }
        }
    }

    fun statusEquals(status: CallStatus?): Specification<Call>? {
        return status?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<CallStatus>("status"), it)
            }
        }
    }

    fun priorityEquals(priority: PriorityLevel?): Specification<Call>? {
        return priority?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<PriorityLevel>("priority"), it)
            }
        }
    }

    fun citizenNameContains(name: String?): Specification<Call>? {
        return name?.let {
            Specification { root, _, criteriaBuilder ->
                val citizenJoin = root.join<Call, String>("citizen", JoinType.LEFT)
                criteriaBuilder.like(criteriaBuilder.lower(citizenJoin.get("firstName")), "%${it.lowercase()}%")
            }
        }
    }

    fun municipalEmployeeNameContains(name: String?): Specification<Call>? {
        return name?.let {
            Specification { root, _, criteriaBuilder ->
                val employeeJoin = root.join<Call, String>("assignedTo", JoinType.LEFT)
                criteriaBuilder.like(criteriaBuilder.lower(employeeJoin.get("firstName")), "%${it.lowercase()}%")
            }
        }
    }

    fun createdAtEquals(createdAt: LocalDateTime?): Specification<Call>? {
        return createdAt?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<LocalDateTime>("createdAt"), it)
            }
        }
    }

    fun closedAtEquals(closedAt: LocalDateTime?): Specification<Call>? {
        return closedAt?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<LocalDateTime>("closedAt"), it)
            }
        }
    }

    fun estimatedCompletionEquals(estimatedCompletion: LocalDateTime?): Specification<Call>? {
        return estimatedCompletion?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<LocalDateTime>("estimatedCompletion"), it)
            }
        }
    }
}