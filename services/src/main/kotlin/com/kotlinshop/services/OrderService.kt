package com.kotlinshop.services

import com.kotlinshop.domain.exceptions.ForbiddenException
import com.kotlinshop.domain.exceptions.OrderCancellationException
import com.kotlinshop.domain.exceptions.OrderNotFoundException
import com.kotlinshop.domain.exceptions.ProductOutOfStockException
import com.kotlinshop.domain.models.Order
import com.kotlinshop.domain.models.OrderItemInput
import com.kotlinshop.domain.models.OrderStatus
import com.kotlinshop.domain.repositories.AuditLogRepository
import com.kotlinshop.domain.repositories.OrderRepository

class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val auditLogRepository: AuditLogRepository,
    private val eventPublisher: OrderEventPort? = null,
    private val cache: OrderCachePort? = null
) {
    fun createOrder(userId: Int, items: List<OrderItemInput>): Order {
        val total = items.sumOf { item ->
            val product = productService.getById(item.productId)
            if (product.stock < item.quantity)
                throw ProductOutOfStockException(item.productId, product.stock)
            product.price * item.quantity
        }

        items.forEach { item ->
            if (!productService.decreaseStock(item.productId, item.quantity))
                throw ProductOutOfStockException(item.productId, 0)
        }

        val order = orderRepository.create(userId, items, total)
        cache?.set(order)

        auditLogRepository.create(userId, "CREATE_ORDER", "order", order.id, "items=${items.size}")
        eventPublisher?.publishOrderCreated(order)

        return order
    }

    fun getOrders(userId: Int): List<Order> = orderRepository.findByUserId(userId)

    fun getOrderById(orderId: Int, userId: Int): Order {
        cache?.get(orderId)?.let { if (it.userId == userId) return it }
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException(orderId)
        if (order.userId != userId) throw ForbiddenException("Not your order")
        return order
    }

    fun cancelOrder(orderId: Int, userId: Int): Order {
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException(orderId)
        if (order.userId != userId) throw ForbiddenException("Not your order")
        if (order.status == OrderStatus.CANCELLED) throw OrderCancellationException("Order already cancelled")

        orderRepository.updateStatus(orderId, OrderStatus.CANCELLED)
        cache?.invalidate(orderId)
        order.items.forEach { item ->
            productService.decreaseStock(item.productId, -item.quantity)
        }
        auditLogRepository.create(userId, "CANCEL_ORDER", "order", orderId, null)
        return orderRepository.findById(orderId)!!
    }
}

interface OrderEventPort {
    fun publishOrderCreated(order: Order)
}

interface OrderCachePort {
    fun get(id: Int): Order?
    fun set(order: Order)
    fun invalidate(id: Int)
}
