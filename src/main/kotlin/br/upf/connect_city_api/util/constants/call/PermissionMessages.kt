package br.upf.connect_city_api.util.constants.call

object PermissionMessages {
    const val CITIZEN_NOT_IDENTIFIED = "Cidadão não identificado. Verificação de permissões falhou."
    const val CALL_WITHOUT_CITIZEN = "Este chamado não pertence a um cidadão válido. Acesso negado."
    const val CITIZEN_NO_PERMISSION = "Você não tem permissão para acessar este chamado."

    const val EMPLOYEE_NOT_IDENTIFIED = "Funcionário municipal não identificado. Verificação de permissões falhou."
    const val CALL_WITHOUT_EMPLOYEE = "Este chamado não está associado a um funcionário válido. Acesso negado."
    const val EMPLOYEE_NO_PERMISSION = "Você não tem permissão para acessar este chamado como funcionário."

    const val MANAGER_NOT_IDENTIFIED = "Funcionário não identificado. Não foi possível verificar se é um gerente."
    const val MANAGER_NO_ASSIGNMENT = "Este chamado não foi atribuído a um funcionário. Acesso negado para o gerente."
    const val MANAGER_NO_PERMISSION = "Você não tem permissão para gerenciar este chamado."

    const val CALL_ASSIGNED_TO_EMPLOYEE = "O chamado já foi atribuído a um funcionário. O criador não pode mais atualizá-lo."
}