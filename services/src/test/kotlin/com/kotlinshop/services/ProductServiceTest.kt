package com.kotlinshop.services

import com.kotlinshop.domain.exceptions.ProductNotFoundException
import com.kotlinshop.domain.models.Product
import com.kotlinshop.domain.repositories.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProductServiceTest {

    private val productRepository = mockk<ProductRepository>()
    private val productService = ProductService(productRepository)

    private val sampleProduct = Product(1, "Widget", "A widget", 9.99, 100, 0L, 0L)

    @Test
    fun `getById returns product when it exists`() {
        every { productRepository.findById(1) } returns sampleProduct

        val result = productService.getById(1)

        assertEquals(sampleProduct, result)
    }

    @Test
    fun `getById throws ProductNotFoundException when product does not exist`() {
        every { productRepository.findById(99) } returns null

        assertFailsWith<ProductNotFoundException> {
            productService.getById(99)
        }
    }

    @Test
    fun `create delegates to repository and returns created product`() {
        every { productRepository.create("Widget", "A widget", 9.99, 100) } returns sampleProduct

        val result = productService.create("Widget", "A widget", 9.99, 100)

        assertEquals(sampleProduct, result)
        verify { productRepository.create("Widget", "A widget", 9.99, 100) }
    }
}
