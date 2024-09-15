package br.upf.connect_city_api.config.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class SanitizingStringDeserializer : JsonDeserializer<String?>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String? {
        val value = p.valueAsString
        return value?.trim()?.let { SanitizationUtils.sanitizeInput(it) }
    }
}