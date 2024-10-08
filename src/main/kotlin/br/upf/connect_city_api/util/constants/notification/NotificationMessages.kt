package br.upf.connect_city_api.util.constants.notification

object NotificationMessages {
    // Mensagens Gerais
    const val NOTIFICATION_SENT_SUCCESSFULLY = "Notificação enviada com sucesso."
    const val CALL_ASSIGNED = "O chamado foi atribuído a um funcionário."
    const val NOTIFICATION_NOT_FOUND = "Notificação não encontrada."
    const val INVALID_NOTIFICATION_STATUS = "Status de notificação inválido."
    const val NOTIFICATION_STATUS_CHANGE = "Status da notificação alterado com sucesso."

    // Status da Notificação
    const val NOTIFICATION_STATUS_PENDING = "PENDENTE"
    const val NOTIFICATION_STATUS_SENT = "ENVIADA"
    const val NOTIFICATION_STATUS_FAILED = "FALHOU"

    // Tipo de Notificação
    const val NOTIFICATION_TYPE_CALL = "Chamado"
    const val NOTIFICATION_TYPE_GENERAL = "Notificação Geral"

    // Constantes de E-mail
    const val EMAIL_TEMPLATE_ID_CALL_NOTIFICATION = "d-e43fc2dca54345e6a6e30a1b26dbaf88"
    const val EMAIL_SUBJECT_CALL_NOTIFICATION = "Notificação de Chamado: "
    const val EMAIL_SUBJECT_GENERAL_NOTIFICATION = "Nova Notificação"

    // Chaves Dinâmicas do E-mail
    const val EMAIL_DYNAMIC_KEY_CALL_ID = "call_id"
    const val EMAIL_DYNAMIC_KEY_CALL_SUBJECT = "call_subject"
    const val EMAIL_DYNAMIC_KEY_CALL_DESCRIPTION = "call_description"
    const val EMAIL_DYNAMIC_KEY_CALL_STATUS = "call_status"

    // Número máximo de tentativas de envio
    const val MAX_NOTIFICATION_ATTEMPTS = 3
}