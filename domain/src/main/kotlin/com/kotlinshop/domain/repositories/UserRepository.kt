package com.kotlinshop.domain.repositories

import com.kotlinshop.domain.models.User
import com.kotlinshop.domain.models.UserRole

interface UserRepository {
    fun findById(id: Int): User?
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
    fun create(username: String, email: String, passwordHash: String, role: UserRole = UserRole.USER): User
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
    fun countAll(): Long
}
