package com.kotlinshop.domain.models

enum class UserRole { USER, ADMIN }

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val createdAt: Long
)
