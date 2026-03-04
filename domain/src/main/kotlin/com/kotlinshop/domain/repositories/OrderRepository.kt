package com.kotlinshop.domain.repositories

import com.kotlinshop.domain.models.Order
import com.kotlinshop.domain.models.OrderItemInput
import com.kotlinshop.domain.models.OrderStatus

interface OrderRepository {
    fun findById(id: Int): Order?
    fun findByUserId(userId: Int): List<Order>
    fun create(userId: Int, items: List<OrderItemInput>, totalAmount: Double): Order
    fun updateStatus(id: Int, status: OrderStatus): Boolean
    fun findAll(): List<Order>
    fun countByStatus(status: OrderStatus): Long
    fun totalRevenue(): Double
}
