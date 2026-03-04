package com.kotlinshop.infrastructure.db.tables

import com.kotlinshop.domain.models.OrderStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object OrdersTable : IntIdTable("orders") {
    val userId = reference("user_id", UsersTable)
    val status = enumerationByName("status", 20, OrderStatus::class)
    val totalAmount = decimal("total_amount", 10, 2)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
