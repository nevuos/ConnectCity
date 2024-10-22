package br.upf.connect_city_api.util.constants.notification

object NotificationMessages {

    // ===================== Mensagens Gerais =====================
    const val EMAIL_DEFAULT_MESSAGE = "Este é um aviso padrão sobre o seu chamado."
    const val NOTIFICATION_NOT_FOUND = "Notificação não encontrada."
    const val INVALID_NOTIFICATION_STATUS = "Status de notificação inválido."

    // ===================== Mensagens Personalizadas =====================
    const val EMAIL_OPENING_MESSAGE = "Recebemos o seu chamado com as seguintes informações:"
    const val EMAIL_STATUS_UPDATE_MESSAGE = "Seu chamado foi atualizado para o seguinte status:"

    const val EMAIL_MESSAGE_IN_PROGRESS = "O chamado com ID {CALL_ID} agora está em andamento."
    const val EMAIL_MESSAGE_RESOLVED = "O chamado com ID {CALL_ID} foi resolvido com sucesso."
    const val EMAIL_MESSAGE_CLOSED = "O chamado com ID {CALL_ID} foi fechado."
    const val EMAIL_MESSAGE_GENERIC = "O chamado com ID {CALL_ID} está no status {STATUS}."
    const val FAILED_NOTIFICATION_MESSAGE = "A tentativa de notificação falhou anteriormente para o chamado com ID: {CALL_ID}."

    // ===================== Mensagens de Criação e Atribuição =====================
    const val CALL_ASSIGNED_MESSAGE = "O chamado foi atribuído ao funcionário: {EMPLOYEE_NAME}."
    const val CALL_CREATED_MESSAGE_CITIZEN = "O chamado com ID {CALL_ID} foi criado com sucesso pelo cidadão."
    const val CALL_CREATED_MESSAGE_EMPLOYEE = "O chamado com ID {CALL_ID} foi criado com sucesso pelo funcionário municipal."

    // ===================== Status da Notificação =====================
    const val NOTIFICATION_STATUS_PENDING = "PENDENTE"
    const val NOTIFICATION_STATUS_SENT = "ENVIADA"
    const val NOTIFICATION_STATUS_FAILED = "FALHOU"

    // ===================== Tipos de Notificação =====================
    const val NOTIFICATION_TYPE_CALL = "CHAMADO"
    const val NOTIFICATION_TYPE_GENERAL = "Notificação Geral"

    // ===================== Configurações de Tentativas =====================
    const val MAX_NOTIFICATION_ATTEMPTS = 3

    // ===================== Constantes de E-mail =====================
    const val EMAIL_TEMPLATE_ID_CALL_NOTIFICATION = "d-e43fc2dca54345e6a6e30a1b26dbaf88"
    const val EMAIL_SUBJECT_CALL_NOTIFICATION = "Notificação de Chamado: "
    const val EMAIL_SUBJECT_GENERAL_NOTIFICATION = "Nova Notificação"

    // ===================== Chaves Dinâmicas do E-mail =====================
    const val EMAIL_DYNAMIC_KEY_CALL_ID = "call_id"
    const val EMAIL_DYNAMIC_KEY_CALL_SUBJECT = "call_subject"
    const val EMAIL_DYNAMIC_KEY_CALL_DESCRIPTION = "call_description"
    const val EMAIL_DYNAMIC_KEY_CALL_STATUS = "call_status"
    const val EMAIL_DYNAMIC_KEY_CUSTOM_MESSAGE = "customMessage"
}