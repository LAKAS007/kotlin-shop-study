package com.kotlinshop.infrastructure.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ProductsTable : IntIdTable("products") {
    val name = varchar("name", 200)
    val description = text("description")
    val price = decimal("price", 10, 2)
    val stock = integer("stock")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
