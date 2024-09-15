package br.upf.connect_city_api.repository.specifications

import br.upf.connect_city_api.model.entity.user.Citizen
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object CitizenSpecifications {

    fun firstNameContains(firstName: String?): Specification<Citizen>? {
        return firstName?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get("firstName")), "%${it.lowercase()}%")
            }
        }
    }

    fun lastNameContains(lastName: String?): Specification<Citizen>? {
        return lastName?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get("lastName")), "%${it.lowercase()}%")
            }
        }
    }

    fun cpfEquals(cpf: String?): Specification<Citizen>? {
        return cpf?.let {
            Specification { root, _, cb ->
                cb.equal(root.get<String>("cpf"), it)
            }
        }
    }

    fun dateOfBirthEquals(dateOfBirth: LocalDate?): Specification<Citizen>? {
        return dateOfBirth?.let {
            Specification { root, _, cb ->
                cb.equal(root.get<LocalDate>("dateOfBirth"), it)
            }
        }
    }

    fun genderEquals(gender: String?): Specification<Citizen>? {
        return gender?.let {
            Specification { root, _, cb ->
                cb.equal(cb.lower(root.get("gender")), it.lowercase())
            }
        }
    }

    fun phoneNumberContains(phoneNumber: String?): Specification<Citizen>? {
        return phoneNumber?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get("phoneNumber")), "%${it.lowercase()}%")
            }
        }
    }
}