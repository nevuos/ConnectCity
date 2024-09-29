package br.upf.connect_city_api.util.constants.logging

object TimeLogMessages {
    const val STEP_STARTED_LOG = "Passo iniciado para o stepId: %d pelo funcionário: %d"
    const val STEP_COMPLETED_LOG = "Passo concluído para o stepId: %d pelo funcionário: %d com tempo gasto: %d minutos"
    const val CALL_COMPLETED_LOG =
        "Chamado concluído para o callId: %d pelo funcionário: %d com tempo total gasto: %d minutos"
    const val TOTAL_TIME_SPENT_FOR_STEP = "Tempo total gasto para o stepId: %d é %d minutos"
    const val TOTAL_TIME_SPENT_FOR_CALL = "Tempo total gasto para o callId: %d é %d minutos"
    const val TIME_LOG_CREATED = "Registro de tempo criado para o stepId: %d pelo funcionário: %d"
    const val NO_START_LOG_FOUND = "Nenhum registro de início encontrado para o stepId: %d"
}