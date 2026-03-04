package com.kotlinshop.services

import com.kotlinshop.domain.exceptions.ProductNotFoundException
import com.kotlinshop.domain.exceptions.ProductOutOfStockException
import com.kotlinshop.domain.models.Order
import com.kotlinshop.domain.models.OrderItemInput
import com.kotlinshop.domain.models.OrderStatus
import com.kotlinshop.domain.models.Product
import com.kotlinshop.domain.repositories.AuditLogRepository
import com.kotlinshop.domain.repositories.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrderServiceTest {

    private val orderRepository = mockk<OrderRepository>()
    private val productService = mockk<ProductService>()
    private val auditLogRepository = mockk<AuditLogRepository>(relaxed = true)
    private val orderService = OrderService(orderRepository, productService, auditLogRepository)

    @Test
    fun `createOrder throws ProductNotFoundException when product does not exist`() {
        every { productService.getById(42) } throws ProductNotFoundException(42)

        assertFailsWith<ProductNotFoundException> {
            orderService.createOrder(1, listOf(OrderItemInput(42, 1)))
        }
    }

    @Test
    fun `createOrder throws ProductOutOfStockException when stock is insufficient`() {
        val product = Product(1, "Widget", "A widget", 9.99, 2, 0L, 0L)
        every { productService.getById(1) } returns product

        assertFailsWith<ProductOutOfStockException> {
            orderService.createOrder(1, listOf(OrderItemInput(1, 5)))
        }
    }

    @Test
    fun `createOrder creates order when stock is available`() {
        val product = Product(1, "Widget", "A widget", 9.99, 10, 0L, 0L)
        val expectedOrder = Order(1, 1, OrderStatus.PENDING, 9.99, 0L)
        every { productService.getById(1) } returns product
        every { productService.decreaseStock(1, 1) } returns true
        every { orderRepository.create(1, any(), 9.99) } returns expectedOrder

        val result = orderService.createOrder(1, listOf(OrderItemInput(1, 1)))

        assertEquals(expectedOrder, result)
        verify { productService.decreaseStock(1, 1) }
        verify { orderRepository.create(1, any(), 9.99) }
    }
}
