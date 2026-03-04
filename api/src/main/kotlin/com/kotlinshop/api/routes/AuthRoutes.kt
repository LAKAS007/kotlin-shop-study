package com.kotlinshop.api.routes

import com.kotlinshop.api.dto.LoginRequest
import com.kotlinshop.api.dto.RegisterRequest
import com.kotlinshop.api.dto.TokenResponse
import com.kotlinshop.api.dto.UserResponse
import com.kotlinshop.services.AuthService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    post("/auth/register") {
        val req = call.receive<RegisterRequest>()
        val user = authService.register(req.username, req.email, req.password)
        call.respond(HttpStatusCode.Created, UserResponse(user.id, user.username, user.email, user.role.name, user.createdAt))
    }
    post("/auth/login") {
        val req = call.receive<LoginRequest>()
        val token = authService.login(req.email, req.password)
        call.respond(HttpStatusCode.OK, TokenResponse(token))
    }
}
