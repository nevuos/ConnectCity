package br.upf.connect_city_api.util.constants.auth

class AuthMessages {
    companion object {
        const val ACCESS_TOKEN_RENEWED = "Token de acesso renovado com sucesso."
        const val ACCOUNT_INACTIVE = "Conta inativa. Entre em contato com o suporte."
        const val LOGOUT_SUCCESS_MESSAGE = "Logout bem-sucedido"
        const val INVALID_REFRESH_TOKEN = "Token de atualização inválido ou expirado."
        const val INVALID_TOKEN_CLAIMS = "Reivindicações do token inválidas."
        const val LOGIN_SUCCESS_MESSAGE = "Login bem sucedido"
        const val EMAIL_PENDING_CONFIRMATION = "Por favor, confirme seu e-mail antes de fazer login."
        const val INVALID_CREDENTIALS = "E-mail ou senha incorretos."
        const val ACCOUNT_LOCKED_MESSAGE =
            "Conta temporariamente bloqueada devido a múltiplas tentativas de login falhadas. Por favor, tente novamente mais tarde."
        const val PASSWORD_RESET_REQUEST_LIMIT_REACHED =
            "Muitas tentativas de redefinição de senha. Tente novamente mais tarde."
        const val TOKEN_VALID = "Token de confirmação válido."
        const val TOKEN_INVALID_OR_EXPIRED = "Token de confirmação inválido ou expirado."
        const val EMAIL_CONFIRMATION_MESSAGE = "Um e-mail de confirmação foi enviado para você."
        const val PASSWORD_RESET_REQUEST_ACCEPTED =
            "Se existir uma conta com o e-mail fornecido, você receberá um e-mail de redefinição de senha."
        const val PASSWORD_CHANGED_SUCCESS = "Senha redefinida com sucesso."
        const val EMAIL_CONFIRMED_MESSAGE = "E-mail confirmado com sucesso."
        const val CONFIRMATION_SUBJECT = "Confirme seu e-mail"
        const val RESET_SUBJECT = "Redefinir sua senha"
        const val USER_NOT_AUTHENTICATED = "Usuário não autenticado"
        const val ACCESS_DENIED = "Acesso negado para o seu usuário"
    }
}