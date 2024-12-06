package br.upf.connect_city_api.repository.specifications

import br.upf.connect_city_api.model.entity.call.Category
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.time.LocalDateTime

object CategorySpecifications {

    fun isActive(isActive: Boolean?): Specification<Category>? {
        return isActive?.let {
            Specification.where { root, _, cb ->
                cb.equal(root.get<Boolean>("isActive"), it)
            }
        }
    }

    fun nameContains(name: String?): Specification<Category>? {
        return name?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%")
            }
        }
    }

    fun parentCategoryEquals(parentCategoryId: Long?): Specification<Category> {
        return when {
            parentCategoryId != null -> Specification.where { root, _, cb ->
                cb.equal(root.get<Category>("parentCategory").get<Long>("id"), parentCategoryId)
            }
            else -> Specification.where { root, _, cb ->
                cb.isNull(root.get<Category>("parentCategory"))
            }
        }
    }

    fun createdAfter(start: LocalDateTime?): Specification<Category>? {
        return start?.let {
            Specification.where { root, _, cb ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), it)
            }
        }
    }

    fun createdBefore(end: LocalDateTime?): Specification<Category>? {
        return end?.let {
            Specification.where { root, _, cb ->
                cb.lessThanOrEqualTo(root.get("createdAt"), it)
            }
        }
    }

    fun createdOn(createdOn: LocalDate?): Specification<Category>? {
        return createdOn?.let {
            Specification.where { root, _, cb ->
                cb.equal(
                    cb.function("DATE", LocalDate::class.java, root.get<LocalDateTime>("createdAt")),
                    it
                )
            }
        }
    }

    fun createdByEquals(createdBy: String?): Specification<Category>? {
        return createdBy?.let {
            Specification.where { root, _, cb ->
                cb.equal(cb.lower(root.get("createdBy")), it.lowercase())
            }
        }
    }
}