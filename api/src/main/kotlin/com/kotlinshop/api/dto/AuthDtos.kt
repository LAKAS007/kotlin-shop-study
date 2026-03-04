package com.kotlinshop.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class TokenResponse(val token: String)

@Serializable
data class UserResponse(val id: Int, val username: String, val email: String, val role: String, val createdAt: Long)
