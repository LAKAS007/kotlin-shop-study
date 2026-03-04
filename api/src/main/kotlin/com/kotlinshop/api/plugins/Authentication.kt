package com.kotlinshop.api.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kotlinshop.services.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "kotlinshop"
            verifier(JWT.require(Algorithm.HMAC256(JwtConfig.secret)).withIssuer(JwtConfig.issuer).withAudience(JwtConfig.audience).build())
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token invalid or expired"))
            }
        }
    }
}
