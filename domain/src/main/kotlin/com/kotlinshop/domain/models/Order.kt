package com.kotlinshop.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus { PENDING, CONFIRMED, CANCELLED }

@Serializable
data class Order(
    val id: Int,
    val userId: Int,
    val status: OrderStatus,
    val totalAmount: Double,
    val createdAt: Long,
    val items: List<OrderItem> = emptyList()
)

@Serializable
data class OrderItem(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val quantity: Int,
    val priceAtTime: Double
)

@Serializable
data class OrderItemInput(
    val productId: Int,
    val quantity: Int
)
