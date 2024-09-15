package br.upf.connect_city_api.config.security

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*

@Component
class CSPFilter : Filter {

    companion object {
        const val HEADER_CSP = "Content-Security-Policy"
        const val HEADER_X_CONTENT_SECURITY_POLICY = "X-Content-Security-Policy"
        const val HEADER_X_WEBKIT_CSP = "X-WebKit-CSP"
        const val HEADER_X_XSS_PROTECTION = "X-XSS-Protection"
        const val HEADER_STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security"
        const val HEADER_X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options"
        const val HEADER_REFERRER_POLICY = "Referrer-Policy"
        const val HEADER_X_FRAME_OPTIONS = "X-Frame-Options"
        const val HEADER_PERMISSIONS_POLICY = "Permissions-Policy"

        const val CSP_POLICY_TEMPLATE =
            "default-src 'self'; script-src 'self' 'nonce-%s'; style-src 'self' 'nonce-%s'; object-src 'none'; frame-ancestors 'none'; base-uri 'self'"
        const val XSS_PROTECTION_POLICY = "1; mode=block"
        const val STRICT_TRANSPORT_SECURITY_POLICY = "max-age=31536000; includeSubDomains"
        const val CONTENT_TYPE_OPTIONS_POLICY = "nosniff"
        const val REFERRER_POLICY = "no-referrer"
        const val FRAME_OPTIONS_POLICY = "DENY"
        const val PERMISSIONS_POLICY = "geolocation=(), microphone=(), camera=()"

        const val ATTRIBUTE_CSP_NONCE = "cspNonce"
        const val UUID_REGEX = "-"
        const val EMPTY_STRING = ""
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpResponse = response as HttpServletResponse
        val nonce = UUID.randomUUID().toString().replace(UUID_REGEX, EMPTY_STRING)

        val cspPolicy = String.format(CSP_POLICY_TEMPLATE, nonce, nonce)
        httpResponse.setHeader(HEADER_CSP, cspPolicy)
        httpResponse.setHeader(HEADER_X_CONTENT_SECURITY_POLICY, cspPolicy)
        httpResponse.setHeader(HEADER_X_WEBKIT_CSP, cspPolicy)
        httpResponse.setHeader(HEADER_X_XSS_PROTECTION, XSS_PROTECTION_POLICY)
        httpResponse.setHeader(HEADER_STRICT_TRANSPORT_SECURITY, STRICT_TRANSPORT_SECURITY_POLICY)
        httpResponse.setHeader(HEADER_X_CONTENT_TYPE_OPTIONS, CONTENT_TYPE_OPTIONS_POLICY)
        httpResponse.setHeader(HEADER_REFERRER_POLICY, REFERRER_POLICY)
        httpResponse.setHeader(HEADER_X_FRAME_OPTIONS, FRAME_OPTIONS_POLICY)
        httpResponse.setHeader(HEADER_PERMISSIONS_POLICY, PERMISSIONS_POLICY)

        request.setAttribute(ATTRIBUTE_CSP_NONCE, nonce)
        chain.doFilter(request, response)
    }

    override fun init(filterConfig: FilterConfig) {}
    override fun destroy() {}
}