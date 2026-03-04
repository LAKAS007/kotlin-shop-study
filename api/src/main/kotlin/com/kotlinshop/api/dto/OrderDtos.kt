package com.kotlinshop.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(val productId: Int, val quantity: Int)

@Serializable
data class CreateOrderRequest(val items: List<OrderItemRequest>)

@Serializable
data class OrderItemResponse(val id: Int, val orderId: Int, val productId: Int, val quantity: Int, val priceAtTime: Double)

@Serializable
data class OrderResponse(val id: Int, val userId: Int, val status: String, val totalAmount: Double, val createdAt: Long, val items: List<OrderItemResponse>)

@Serializable
data class StatsResponse(val totalOrders: Long, val pendingOrders: Long, val confirmedOrders: Long, val cancelledOrders: Long, val totalRevenue: Double, val totalProducts: Int, val totalUsers: Long)
