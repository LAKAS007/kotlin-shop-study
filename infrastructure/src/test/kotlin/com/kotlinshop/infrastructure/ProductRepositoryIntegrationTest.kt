package com.kotlinshop.infrastructure

import com.kotlinshop.infrastructure.db.repositories.ProductRepositoryImpl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductRepositoryIntegrationTest : DatabaseTest() {

    private val productRepository = ProductRepositoryImpl()

    @Test
    fun `create and retrieve product returns correct data`() {
        val product = productRepository.create("Gadget", "A cool gadget", 49.99, 50)

        val found = productRepository.findById(product.id)

        assertNotNull(found)
        assertEquals("Gadget", found.name)
        assertEquals("A cool gadget", found.description)
        assertEquals(49.99, found.price)
        assertEquals(50, found.stock)
    }

    @Test
    fun `decreaseStock reduces inventory correctly`() {
        val product = productRepository.create("Widget", "A widget", 9.99, 20)

        val result = productRepository.decreaseStock(product.id, 5)

        assertTrue(result)
        val updated = productRepository.findById(product.id)
        assertNotNull(updated)
        assertEquals(15, updated.stock)
    }
}
