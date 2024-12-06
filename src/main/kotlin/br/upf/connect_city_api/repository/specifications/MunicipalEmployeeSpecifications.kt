package br.upf.connect_city_api.repository.specifications

import br.upf.connect_city_api.model.entity.enums.EmployeeType
import br.upf.connect_city_api.model.entity.user.MunicipalEmployee
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

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

    fun isApprovedEquals(isApproved: Boolean?): Specification<MunicipalEmployee>? {
        return isApproved?.let {
            Specification { root, _, cb -> cb.equal(root.get<Boolean>("isApproved"), it) }
        }
    }

    fun isManagerEquals(isManager: Boolean?): Specification<MunicipalEmployee>? {
        return isManager?.let {
            Specification { root, _, cb -> cb.equal(root.get<Boolean>("isManager"), it) }
        }
    }

    fun dateOfBirthEquals(dateOfBirth: LocalDate?): Specification<MunicipalEmployee>? {
        return dateOfBirth?.let {
            Specification { root, _, cb -> cb.equal(root.get<LocalDate>("dateOfBirth"), it) }
        }
    }

    fun genderEquals(gender: String?): Specification<MunicipalEmployee>? {
        return gender?.let {
            Specification { root, _, cb -> cb.equal(cb.lower(root.get("gender")), it.lowercase()) }
        }
    }

    fun phoneNumberEquals(phoneNumber: String?): Specification<MunicipalEmployee>? {
        return phoneNumber?.let {
            Specification { root, _, cb -> cb.like(root.get("phoneNumber"), "%${it}%") }
        }
    }

    fun employeeTypeEquals(employeeType: EmployeeType?): Specification<MunicipalEmployee>? {
        return employeeType?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<EmployeeType>("employeeType"), it)
            }
        }
    }

}