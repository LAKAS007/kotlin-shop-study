package com.kotlinshop.infrastructure.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object OrderItemsTable : IntIdTable("order_items") {
    val orderId = reference("order_id", OrdersTable)
    val productId = reference("product_id", ProductsTable)
    val quantity = integer("quantity")
    val priceAtTime = decimal("price_at_time", 10, 2)
}
