package com.kotlinshop.services

import com.kotlinshop.domain.models.OrderStatus
import com.kotlinshop.domain.repositories.OrderRepository
import com.kotlinshop.domain.repositories.ProductRepository
import com.kotlinshop.domain.repositories.UserRepository

data class OrderStats(
    val totalOrders: Long,
    val pendingOrders: Long,
    val confirmedOrders: Long,
    val cancelledOrders: Long,
    val totalRevenue: Double,
    val totalProducts: Int,
    val totalUsers: Long
)

class AdminService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {
    fun getStats(): OrderStats = OrderStats(
        totalOrders = orderRepository.findAll().size.toLong(),
        pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING),
        confirmedOrders = orderRepository.countByStatus(OrderStatus.CONFIRMED),
        cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED),
        totalRevenue = orderRepository.totalRevenue(),
        totalProducts = productRepository.findAll().size,
        totalUsers = userRepository.countAll()
    )
}
