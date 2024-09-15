package br.upf.connect_city_api.config.security

import org.owasp.encoder.Encode
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.StrictHttpFirewall

@Configuration
class FirewallConfig {

    @Value("\${firewall.enabled:true}")
    private var firewallEnabled: Boolean = true

    @Value("\${allowed.hostname:.*}")
    private lateinit var allowedHostname: String

    companion object {
        private val HEADER_NAME_REGEX = "^[a-zA-Z0-9-]+$".toRegex()
        private val PARAM_NAME_REGEX = "^[a-zA-Z0-9_]+$".toRegex()
        private val PARAM_VALUE_REGEX = "^[a-zA-Z0-9,;\\s@.:-]+$".toRegex()
        private const val SCRIPT_TAG = "<script>"
    }

    @Bean
    fun strictHttpFirewall(): HttpFirewall {
        val firewall = StrictHttpFirewall()

        if (firewallEnabled) {
            configureFirewallSettings(firewall)
        }

        return firewall
    }

    private fun configureFirewallSettings(firewall: StrictHttpFirewall) {
        firewall.setAllowSemicolon(false)
        firewall.setAllowUrlEncodedSlash(false)
        firewall.setAllowUrlEncodedDoubleSlash(false)
        firewall.setAllowBackSlash(false)
        firewall.setAllowUrlEncodedPeriod(false)
        firewall.setAllowUrlEncodedPercent(false)
        firewall.setAllowUrlEncodedCarriageReturn(false)
        firewall.setAllowUrlEncodedLineFeed(false)
        firewall.setAllowUrlEncodedParagraphSeparator(false)
        firewall.setAllowUrlEncodedLineSeparator(false)
        firewall.setAllowNull(false)

        configureAllowedHeaderNames(firewall)
        configureAllowedParameterNames(firewall)
        configureAllowedHostnames(firewall)
    }

    private fun configureAllowedHeaderNames(firewall: StrictHttpFirewall) {
        firewall.setAllowedHeaderNames { it.matches(HEADER_NAME_REGEX) }
        firewall.setAllowedHeaderValues {
            val sanitizedValue = Encode.forHtml(it)
            !sanitizedValue.contains(SCRIPT_TAG, ignoreCase = true)
        }
    }

    private fun configureAllowedParameterNames(firewall: StrictHttpFirewall) {
        firewall.setAllowedParameterNames { it.matches(PARAM_NAME_REGEX) }
        firewall.setAllowedParameterValues {
            val sanitizedValue = Encode.forHtml(it)
            sanitizedValue.matches(PARAM_VALUE_REGEX)
        }
    }

    private fun configureAllowedHostnames(firewall: StrictHttpFirewall) {
        firewall.setAllowedHostnames { it != null && it.matches(allowedHostname.toRegex()) }
    }
}