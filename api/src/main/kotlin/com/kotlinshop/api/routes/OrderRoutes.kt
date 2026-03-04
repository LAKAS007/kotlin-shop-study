package com.kotlinshop.api.routes

import com.kotlinshop.api.dto.CreateOrderRequest
import com.kotlinshop.api.dto.OrderItemResponse
import com.kotlinshop.api.dto.OrderResponse
import com.kotlinshop.domain.models.Order
import com.kotlinshop.domain.models.OrderItemInput
import com.kotlinshop.services.OrderService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private fun Order.toResponse() = OrderResponse(
    id = id,
    userId = userId,
    status = status.name,
    totalAmount = totalAmount,
    createdAt = createdAt,
    items = items.map { OrderItemResponse(it.id, it.orderId, it.productId, it.quantity, it.priceAtTime) }
)

fun Route.orderRoutes(orderService: OrderService) {
    post("/orders") {
        val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
        val req = call.receive<CreateOrderRequest>()
        val items = req.items.map { OrderItemInput(it.productId, it.quantity) }
        val order = orderService.createOrder(userId, items)
        call.respond(HttpStatusCode.Created, order.toResponse())
    }
    get("/orders") {
        val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
        val orders = orderService.getOrders(userId)
        call.respond(HttpStatusCode.OK, orders.map { it.toResponse() })
    }
    delete("/orders/{id}") {
        val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
        val orderId = call.parameters["id"]!!.toInt()
        val order = orderService.cancelOrder(orderId, userId)
        call.respond(HttpStatusCode.OK, order.toResponse())
    }
}
