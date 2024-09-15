package br.upf.connect_city_api.repository.specifications

import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import org.springframework.data.jpa.domain.Specification

object MunicipalEmployeeSpecifications {

    fun firstNameContains(firstName: String?): Specification<MunicipalEmployee>? {
        return firstName?.let {
            Specification { root, _, cb -> cb.like(cb.lower(root.get("firstName")), "%${it.lowercase()}%") }
        }
    }

    fun lastNameContains(lastName: String?): Specification<MunicipalEmployee>? {
        return lastName?.let {
            Specification { root, _, cb -> cb.like(cb.lower(root.get("lastName")), "%${it.lowercase()}%") }
        }
    }

    fun cpfEquals(cpf: String?): Specification<MunicipalEmployee>? {
        return cpf?.let {
            Specification { root, _, cb -> cb.equal(root.get<String>("cpf"), it) }
        }
    }

    fun jobTitleContains(jobTitle: String?): Specification<MunicipalEmployee>? {
        return jobTitle?.let {
            Specification { root, _, cb -> cb.like(cb.lower(root.get("jobTitle")), "%${it.lowercase()}%") }
        }
    }

    fun departmentContains(department: String?): Specification<MunicipalEmployee>? {
        return department?.let {
            Specification { root, _, cb -> cb.like(cb.lower(root.get("department")), "%${it.lowercase()}%") }
        }
    }
}