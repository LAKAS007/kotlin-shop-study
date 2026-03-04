package com.kotlinshop.services

import com.kotlinshop.domain.exceptions.EmailAlreadyExistsException
import com.kotlinshop.domain.exceptions.UnauthorizedException
import com.kotlinshop.domain.models.User
import com.kotlinshop.domain.models.UserRole
import com.kotlinshop.domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val authService = AuthService(userRepository)

    @Test
    fun `register creates user when email not taken`() {
        every { userRepository.existsByEmail("test@example.com") } returns false
        every { userRepository.existsByUsername("testuser") } returns false
        val createdUser = User(1, "testuser", "test@example.com", "hash", UserRole.USER, 0L)
        every { userRepository.create("testuser", "test@example.com", any()) } returns createdUser

        val result = authService.register("testuser", "test@example.com", "password123")

        assertEquals(createdUser, result)
        verify { userRepository.create("testuser", "test@example.com", any()) }
    }

    @Test
    fun `register throws EmailAlreadyExistsException when email is taken`() {
        every { userRepository.existsByEmail("taken@example.com") } returns true

        assertFailsWith<EmailAlreadyExistsException> {
            authService.register("testuser", "taken@example.com", "password123")
        }
    }

    @Test
    fun `login throws UnauthorizedException when user not found`() {
        every { userRepository.findByEmail("missing@example.com") } returns null

        assertFailsWith<UnauthorizedException> {
            authService.login("missing@example.com", "password123")
        }
    }
}
