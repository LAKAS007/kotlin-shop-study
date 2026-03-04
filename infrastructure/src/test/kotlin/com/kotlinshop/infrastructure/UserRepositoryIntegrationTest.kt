package com.kotlinshop.infrastructure

import com.kotlinshop.infrastructure.db.repositories.UserRepositoryImpl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserRepositoryIntegrationTest : DatabaseTest() {

    private val userRepository = UserRepositoryImpl()

    @Test
    fun `create and find by email returns created user`() {
        val user = userRepository.create("alice", "alice@example.com", "hashedpassword")

        val found = userRepository.findByEmail("alice@example.com")

        assertNotNull(found)
        assertEquals("alice", found.username)
        assertEquals("alice@example.com", found.email)
        assertEquals(user.id, found.id)
    }

    @Test
    fun `existsByEmail returns correct results`() {
        userRepository.create("bob", "bob@example.com", "hashedpassword")

        assertTrue(userRepository.existsByEmail("bob@example.com"))
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"))
    }
}
