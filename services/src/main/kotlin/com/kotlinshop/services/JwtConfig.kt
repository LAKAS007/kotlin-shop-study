package com.kotlinshop.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import java.util.Date

object JwtConfig {
    private const val EXPIRATION_MS = 86400000L

    lateinit var secret: String
    lateinit var issuer: String
    lateinit var audience: String

    val algorithm get() = Algorithm.HMAC256(secret)
    val verifier: JWTVerifier get() = JWT.require(algorithm).withIssuer(issuer).withAudience(audience).build()

    fun generateToken(userId: Int, role: String): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_MS))
            .sign(algorithm)
}
