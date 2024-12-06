package br.upf.connect_city_api.dtos.employee

import br.upf.connect_city_api.model.entity.enums.EmployeeType
import java.time.LocalDate

data class MunicipalEmployeeDetailsDTO(
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val cpf: String? = null,
    val phoneNumber: String? = null,
    val jobTitle: String? = null,
    val department: String? = null,
    val employeeType: EmployeeType? = null,
    val isApproved: Boolean? = null,
    val dateOfBirth: LocalDate? = null,
    val gender: String? = null,
    val isManager: Boolean? = null
)