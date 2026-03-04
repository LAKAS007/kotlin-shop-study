package com.kotlinshop.api

import com.kotlinshop.api.plugins.configureAuthentication
import com.kotlinshop.api.plugins.configureSerialization
import com.kotlinshop.api.plugins.configureStatusPages
import com.kotlinshop.domain.exceptions.ProductNotFoundException
import com.kotlinshop.domain.models.Product
import com.kotlinshop.domain.repositories.ProductRepository
import com.kotlinshop.services.AdminService
import com.kotlinshop.services.AuthService
import com.kotlinshop.services.JwtConfig
import com.kotlinshop.services.OrderService
import com.kotlinshop.services.ProductService
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductE2ETest {

    private val productRepository = mockk<ProductRepository>()
    private val productService = ProductService(productRepository)
    private val authService = mockk<AuthService>(relaxed = true)
    private val orderService = mockk<OrderService>(relaxed = true)
    private val adminService = mockk<AdminService>(relaxed = true)

    @BeforeTest
    fun setup() {
        JwtConfig.secret = "test-secret-key-32-chars-minimum!!"
        JwtConfig.issuer = "test"
        JwtConfig.audience = "test"
    }

    @Test
    fun `GET products returns list`() = testApplication {
        application {
            configureSerialization()
            configureAuthentication()
            configureStatusPages()
            configureRouting(authService, productService, orderService, adminService)
        }

        val products = listOf(
            Product(1, "Widget", "A widget", 9.99, 100, 0L, 0L),
            Product(2, "Gadget", "A gadget", 19.99, 50, 0L, 0L)
        )
        every { productRepository.findAll() } returns products

        val response = client.get("/products")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET products by id returns 404 when not found`() = testApplication {
        application {
            configureSerialization()
            configureAuthentication()
            configureStatusPages()
            configureRouting(authService, productService, orderService, adminService)
        }

        every { productRepository.findById(999) } returns null

        val response = client.get("/products/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
