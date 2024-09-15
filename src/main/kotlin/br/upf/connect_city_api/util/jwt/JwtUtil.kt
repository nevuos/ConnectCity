package br.upf.connect_city_api.util.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    @Value("\${jwt.secretKey}")
    private lateinit var secretKey: String

    @Value("\${jwt.accessTokenExpirationMs}")
    private var accessTokenExpirationMs: Long = 0

    @Value("\${jwt.refreshTokenExpirationMs}")
    private var refreshTokenExpirationMs: Long = 0

    private fun generateToken(claimKey: String, claimValue: Any, expirationMs: Long): String {
        val expirationDate = Date(System.currentTimeMillis() + expirationMs)
        return JWT.create()
            .withClaim(claimKey, claimValue.toString())
            .withExpiresAt(expirationDate)
            .sign(Algorithm.HMAC256(secretKey))
    }

    fun generateAccessToken(claimKey: String, claimValue: Any): String {
        return generateToken(claimKey, claimValue, accessTokenExpirationMs)
    }

    fun generateRefreshToken(claimKey: String, claimValue: Any): String {
        return generateToken(claimKey, claimValue, refreshTokenExpirationMs)
    }

    fun isValidToken(token: String): Boolean {
        return try {
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }

    fun getTokenExpirationTime(token: String?): Long? {
        return try {
            token?.let {
                val decodedToken: DecodedJWT = JWT.decode(it)
                val expirationDate: Date? = decodedToken.expiresAt
                expirationDate?.let {
                    val currentTime = Date(System.currentTimeMillis())
                    val timeRemainingMillis = expirationDate.time - currentTime.time
                    if (timeRemainingMillis > 0) timeRemainingMillis else null
                }
            }
        } catch (e: JWTVerificationException) {
            null
        }
    }

    fun getClaim(token: String, claimKey: String): String? {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(secretKey)).build()
            verifier.verify(token).getClaim(claimKey)?.asString()
        } catch (e: JWTVerificationException) {
            null
        }
    }
}