package br.upf.connect_city_api.util.constants.call

object CallMessages {

    // Mensagens de Sucesso
    const val CALL_CREATED_SUCCESSFULLY = "Chamado criado com sucesso."
    const val CALL_UPDATED_SUCCESSFULLY = "Chamado atualizado com sucesso."
    const val CALL_ASSIGNED_SUCCESSFULLY = "Chamado atribuído com sucesso."
    const val CALL_COMPLETED_LOG = "Chamado concluído com sucesso."
    const val CALL_DELETED_SUCCESSFULLY = "Chamado excluído com sucesso."

    // Mensagens de Erro
    const val CALL_NOT_FOUND = "Chamado não encontrado."
    const val STEP_NOT_FOUND = "Etapa não encontrada."
    const val CATEGORY_NOT_FOUND = "Categoria não encontrada com o ID fornecido."
    const val CALL_ID_CANNOT_BE_NULL = "ID do chamado não pode ser nulo."
    const val INVALID_STATUS_UPDATE = "Status de chamado inválido."
    const val CATEGORY_ALREADY_EXISTS = "A categoria já existe."
    const val INVALID_CATEGORY = "Categoria inválida fornecida."
    const val CALL_CREATION_FAILED = "Falha ao criar o chamado."

    // Mensagens de Categorias
    const val CATEGORY_CREATED_SUCCESS = "Categoria criada com sucesso."
    const val CATEGORY_UPDATED_SUCCESS = "Categoria atualizada com sucesso."
    const val CATEGORY_DELETED_SUCCESS = "Categoria excluída com sucesso."

    // Mensagens de Acesso
    const val ACCESS_DENIED = "Você não tem permissão para acessar este chamado."
    const val ONLY_MANAGER_CAN_ASSIGN =
        "Somente gerentes podem fazer atribuições de chamados após a primeira atribuição."

    // Mensagens de Interações
    const val INTERACTION_ADDED_SUCCESSFULLY = "Interação adicionada com sucesso ao chamado."
    const val INTERACTION_REMOVED_SUCCESSFULLY = "Interação removida com sucesso."
    const val INTERACTION_NOT_BELONG_TO_CALL = "A interação não pertence ao chamado fornecido."
    const val INTERACTION_REMOVAL_FAILED = "Falha ao remover a interação."
    const val INTERACTION_NOT_FOUND = "Interação não encontrada."

    // Mensagens de Status de Chamados
    const val CALL_STATUS_OPEN = "Chamado aberto."
    const val CALL_STATUS_IN_PROGRESS = "Chamado em andamento."
    const val CALL_STATUS_RESOLVED = "Chamado resolvido."
    const val CALL_STATUS_CLOSED = "Chamado encerrado."
    const val CALL_STATUS_CANCELLED = "Chamado cancelado."

    // Mensagens de Steps (etapas do chamado)
    const val STEP_ADDED_SUCCESSFULLY = "Etapa adicionada com sucesso ao chamado."
    const val STEP_UPDATED_SUCCESSFULLY = "Etapa atualizada com sucesso."
    const val STEP_ACCESS_DENIED = "Acesso negado. Você não está autorizado a acessar este passo."
    const val STEP_DELETED_SUCCESSFULLY = "Etapa excluída com sucesso."
    const val STEP_COMPLETED_LOG = "Etapa concluída com sucesso."
    const val STEP_STARTED_LOG = "Etapa iniciada com sucesso."
}