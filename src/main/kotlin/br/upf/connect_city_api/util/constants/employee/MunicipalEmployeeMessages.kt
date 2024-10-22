package br.upf.connect_city_api.util.constants.employee

object MunicipalEmployeeMessages {
    const val EMPLOYEE_CREATED_SUCCESSFULLY = "Funcionário municipal criado com sucesso."
    const val EMPLOYEE_UPDATED_SUCCESSFULLY = "Dados do funcionário municipal atualizados com sucesso."
    const val ONLY_MANAGER_CAN_BE_ASSIGNED_FIRST =
        "Somente gerentes podem ser atribuídos na primeira atribuição de um chamado."
    const val EMPLOYEE_NOT_FOUND = "Funcionário municipal não encontrado."
    const val EMPLOYEE_APPROVED_SUCCESSFULLY = "Funcionário municipal aprovado com sucesso."
    const val EMPLOYEE_ALREADY_APPROVED = "Funcionário municipal já foi aprovado."
    const val ADMIN_NOT_FOUND = "Administrador não encontrado."
    const val INVALID_EMPLOYEE_TYPE = "Tipo de funcionário inválido. Deve ser 'INTERNAL' ou 'EXTERNAL'."
    const val MANAGER_APPROVED_SUCCESSFULLY = "O funcionário municipal foi aprovado como gerente com sucesso."
    const val MANAGER_REMOVED_SUCCESSFULLY = "O status de gerente foi removido do funcionário municipal com sucesso."
    const val EMPLOYEE_NOT_APPROVED = "O funcionário municipal não está aprovado para realizar essa operação."
    const val EMPLOYEE_NOT_MANAGER = "Esta operação requer um gerente. O funcionário não possui permissões de gerente."
}