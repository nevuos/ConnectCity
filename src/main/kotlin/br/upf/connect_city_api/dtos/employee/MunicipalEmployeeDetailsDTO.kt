package br.upf.connect_city_api.dtos.employee

import br.upf.connect_city_api.model.entity.enums.EmployeeType

data class MunicipalEmployeeDetailsDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val cpf: String,
    val phoneNumber: String,
    val jobTitle: String,
    val department: String,
    val employeeType: EmployeeType,
    val isApproved: Boolean
)