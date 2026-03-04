package com.kotlinshop.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.kotlinshop.domain.exceptions.EmailAlreadyExistsException
import com.kotlinshop.domain.exceptions.UnauthorizedException
import com.kotlinshop.domain.exceptions.UsernameAlreadyExistsException
import com.kotlinshop.domain.models.User
import com.kotlinshop.domain.repositories.UserRepository

class AuthService(private val userRepository: UserRepository) {
    fun register(username: String, email: String, password: String): User {
        if (userRepository.existsByEmail(email)) throw EmailAlreadyExistsException(email)
        if (userRepository.existsByUsername(username)) throw UsernameAlreadyExistsException(username)
        val hash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        return userRepository.create(username, email, hash)
    }

    fun login(email: String, password: String): String {
        val user = userRepository.findByEmail(email) ?: throw UnauthorizedException("Invalid credentials")
        val result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash)
        if (!result.verified) throw UnauthorizedException("Invalid credentials")
        return JwtConfig.generateToken(user.id, user.role.name)
    }
}
