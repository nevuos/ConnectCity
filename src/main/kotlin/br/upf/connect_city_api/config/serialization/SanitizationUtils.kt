package br.upf.connect_city_api.config.serialization

import org.owasp.encoder.Encode
import java.util.*

object SanitizationUtils {
    fun sanitizeInput(input: String): String {
        val sanitizedInput = Encode.forHtml(input)
        return if (containsMaliciousPattern(sanitizedInput)) {
            ""
        } else {
            sanitizedInput.replace(Regex("[^\\p{L}\\p{N}\\s@.-]"), "")
        }
    }

    private fun containsMaliciousPattern(input: String): Boolean {
        val maliciousPatterns = listOf(
            "drop table", "select", "insert", "delete", "update", "--", ";", "'", "<script>", "</script>", "<", ">",
            "create", "alter", "grant", "revoke", "union", "order by", "group by", "shutdown", "exec"
        )
        val lowerInput = input.lowercase(Locale.getDefault())
        return maliciousPatterns.any { lowerInput.contains(it) }
    }
}
