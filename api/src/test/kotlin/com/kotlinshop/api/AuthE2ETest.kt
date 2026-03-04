package com.kotlinshop.api

import at.favre.lib.crypto.bcrypt.BCrypt
import com.kotlinshop.api.plugins.configureAuthentication
import com.kotlinshop.api.plugins.configureSerialization
import com.kotlinshop.api.plugins.configureStatusPages
import com.kotlinshop.domain.models.User
import com.kotlinshop.domain.models.UserRole
import com.kotlinshop.domain.repositories.UserRepository
import com.kotlinshop.services.AdminService
import com.kotlinshop.services.AuthService
import com.kotlinshop.services.JwtConfig
import com.kotlinshop.services.OrderService
import com.kotlinshop.services.ProductService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthE2ETest {

    private val userRepository = mockk<UserRepository>()
    private val authService = AuthService(userRepository)
    private val productService = mockk<ProductService>(relaxed = true)
    private val orderService = mockk<OrderService>(relaxed = true)
    private val adminService = mockk<AdminService>(relaxed = true)

    @BeforeTest
    fun setup() {
        JwtConfig.secret = "test-secret-key-32-chars-minimum!!"
        JwtConfig.issuer = "test"
        JwtConfig.audience = "test"
    }

    @Test
    fun `POST auth register returns 201`() = testApplication {
        application {
            configureSerialization()
            configureAuthentication()
            configureStatusPages()
            configureRouting(authService, productService, orderService, adminService)
        }

        every { userRepository.existsByEmail("new@example.com") } returns false
        every { userRepository.existsByUsername("newuser") } returns false
        every { userRepository.create("newuser", "new@example.com", any()) } returns
            User(1, "newuser", "new@example.com", "hash", UserRole.USER, 0L)

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"newuser","email":"new@example.com","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `POST auth login returns token`() = testApplication {
        application {
            configureSerialization()
            configureAuthentication()
            configureStatusPages()
            configureRouting(authService, productService, orderService, adminService)
        }

        val passwordHash = BCrypt.withDefaults().hashToString(12, "password123".toCharArray())
        every { userRepository.findByEmail("user@example.com") } returns
            User(1, "user", "user@example.com", passwordHash, UserRole.USER, 0L)

        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"user@example.com","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("token"))
    }
}
