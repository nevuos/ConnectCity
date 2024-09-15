package br.upf.connect_city_api.service.communication

import br.upf.connect_city_api.util.exception.EmailSendError
import br.upf.connect_city_api.util.exception.InvalidRequestError
import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class EmailService(
    @Value("\${sendgrid.emailUser}") private val fromAddress: String,
    @Value("\${sendgrid.apiKey}") private val apiKey: String
) {

    companion object {
        private const val SUCCESS_MESSAGE = "E-mail enviado com sucesso."
        private const val ERROR_PREFIX = "Falha ao enviar o e-mail"
        private const val PARAMETERS_ERROR = "Parâmetros inválidos."
        private const val RETRIES_EXCEEDED = "Falha ao enviar: Tentativas máximas alcançadas sem sucesso."
        private const val MAX_RETRIES = 3
        private const val INITIAL_RETRY_DELAY_SECONDS = 2L
    }

    @Async
    fun send(to: String, subject: String, confirmationUrl: String, templateId: String): CompletableFuture<String> {
        validateParameters(to, subject, confirmationUrl, templateId)

        return CompletableFuture.supplyAsync {
            val mail = createMail(to, subject, confirmationUrl, templateId)
            val sendGrid = SendGrid(apiKey)
            sendWithRetry(sendGrid, mail)
        }
    }

    private fun validateParameters(to: String, subject: String, confirmationUrl: String, templateId: String) {
        if (to.isBlank() || subject.isBlank() || confirmationUrl.isBlank() || templateId.isBlank()) {
            throw InvalidRequestError(PARAMETERS_ERROR)
        }
    }

    private fun createMail(to: String, subject: String, confirmationUrl: String, templateId: String): Mail {
        val from = Email(fromAddress)
        val toEmail = Email(to)

        return Mail().apply {
            this.from = from
            this.subject = subject
            this.templateId = templateId
            val personalization = Personalization().apply {
                addTo(toEmail)
                addDynamicTemplateData("confirmation_url", confirmationUrl)
            }
            addPersonalization(personalization)
        }
    }

    private fun sendWithRetry(sendGrid: SendGrid, mail: Mail): String {
        var retryDelay = INITIAL_RETRY_DELAY_SECONDS

        repeat(MAX_RETRIES) { attempt ->
            try {
                val response = sendGrid.api(createRequest(mail))
                if (response.statusCode == 202) {
                    return SUCCESS_MESSAGE
                } else {
                    throw EmailSendError("$ERROR_PREFIX: ${response.statusCode}")
                }
            } catch (e: IOException) {
                if (attempt < MAX_RETRIES - 1) {
                    TimeUnit.SECONDS.sleep(retryDelay)
                    retryDelay *= 2
                } else {
                    throw EmailSendError("$ERROR_PREFIX: ${e.message}")
                }
            }
        }
        throw EmailSendError(RETRIES_EXCEEDED)
    }

    private fun createRequest(mail: Mail): Request {
        return Request().apply {
            method = Method.POST
            endpoint = "mail/send"
            body = mail.build()
        }
    }
}