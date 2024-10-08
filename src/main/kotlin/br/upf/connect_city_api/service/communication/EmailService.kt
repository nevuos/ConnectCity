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
        const val SUCCESS_MESSAGE = "E-mail enviado com sucesso."
        const val ERROR_PREFIX = "Falha ao enviar o e-mail"
        const val PARAMETERS_ERROR = "Parâmetros inválidos."
        const val RETRIES_EXCEEDED = "Falha ao enviar: Tentativas máximas alcançadas sem sucesso."
        const val MAX_RETRIES = 3
        const val INITIAL_RETRY_DELAY_SECONDS = 2L
        const val SENDGRID_API_ENDPOINT = "mail/send"
        const val STATUS_CODE_SUCCESS = 202
        const val CONFIRMATION_URL_KEY = "confirmation_url"
    }

    @Async
    fun send(
        to: String,
        subject: String,
        templateId: String,
        dynamicData: Map<String, String> = emptyMap(),
        confirmationUrl: String? = null
    ): CompletableFuture<String> {
        validateParameters(to, subject, templateId)

        val finalDynamicData = if (confirmationUrl != null) {
            dynamicData + (CONFIRMATION_URL_KEY to confirmationUrl)
        } else {
            dynamicData
        }

        val mail = createMailWithDynamicData(to, subject, finalDynamicData, templateId)
        val sendGrid = SendGrid(apiKey)

        return sendWithRetry(sendGrid, mail, 0, INITIAL_RETRY_DELAY_SECONDS)
    }

    private fun validateParameters(to: String, subject: String, templateId: String) {
        if (to.isBlank() || subject.isBlank() || templateId.isBlank()) {
            throw InvalidRequestError(PARAMETERS_ERROR)
        }
    }

    private fun createMailWithDynamicData(
        to: String,
        subject: String,
        dynamicData: Map<String, String>,
        templateId: String
    ): Mail {
        val from = Email(fromAddress)
        val toEmail = Email(to)

        return Mail().apply {
            this.from = from
            this.subject = subject
            this.templateId = templateId
            val personalization = Personalization().apply {
                addTo(toEmail)
                dynamicData.forEach { (key, value) ->
                    addDynamicTemplateData(key, value)
                }
            }
            addPersonalization(personalization)
        }
    }

    private fun sendWithRetry(
        sendGrid: SendGrid,
        mail: Mail,
        attempt: Int,
        delay: Long
    ): CompletableFuture<String> {
        return CompletableFuture.supplyAsync {
            try {
                val response = sendGrid.api(createRequest(mail))
                if (response.statusCode == STATUS_CODE_SUCCESS) {
                    SUCCESS_MESSAGE
                } else {
                    throw EmailSendError("$ERROR_PREFIX: ${response.statusCode}")
                }
            } catch (e: IOException) {
                if (attempt < MAX_RETRIES - 1) {
                    CompletableFuture.delayedExecutor(delay, TimeUnit.SECONDS).execute {
                        sendWithRetry(sendGrid, mail, attempt + 1, delay * 2).get()
                    }
                    throw EmailSendError("$ERROR_PREFIX: ${e.message}")
                } else {
                    throw EmailSendError("$ERROR_PREFIX: $RETRIES_EXCEEDED")
                }
            }
        }
    }

    private fun createRequest(mail: Mail): Request {
        return Request().apply {
            method = Method.POST
            endpoint = SENDGRID_API_ENDPOINT
            body = mail.build()
        }
    }
}