package com.kotlinshop.infrastructure.db.repositories

import com.kotlinshop.domain.models.Order
import com.kotlinshop.domain.models.OrderItem
import com.kotlinshop.domain.models.OrderItemInput
import com.kotlinshop.domain.models.OrderStatus
import com.kotlinshop.domain.repositories.OrderRepository
import com.kotlinshop.infrastructure.db.tables.OrderItemsTable
import com.kotlinshop.infrastructure.db.tables.OrdersTable
import com.kotlinshop.infrastructure.db.tables.ProductsTable
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.time.ZoneOffset

class OrderRepositoryImpl : OrderRepository {

    private fun toOrderItem(row: ResultRow): OrderItem = OrderItem(
        id = row[OrderItemsTable.id].value,
        orderId = row[OrderItemsTable.orderId].value,
        productId = row[OrderItemsTable.productId].value,
        quantity = row[OrderItemsTable.quantity],
        priceAtTime = row[OrderItemsTable.priceAtTime].toDouble()
    )

    private fun toOrder(row: ResultRow, items: List<OrderItem>): Order = Order(
        id = row[OrdersTable.id].value,
        userId = row[OrdersTable.userId].value,
        status = row[OrdersTable.status],
        totalAmount = row[OrdersTable.totalAmount].toDouble(),
        createdAt = row[OrdersTable.createdAt].toJavaLocalDateTime().toEpochSecond(ZoneOffset.UTC),
        items = items
    )

    private fun loadItems(orderId: Int): List<OrderItem> =
        OrderItemsTable.selectAll().where { OrderItemsTable.orderId eq orderId }.map { toOrderItem(it) }

    override fun findById(id: Int): Order? = transaction {
        OrdersTable.selectAll().where { OrdersTable.id eq id }.singleOrNull()?.let { row ->
            toOrder(row, loadItems(id))
        }
    }

    override fun findByUserId(userId: Int): List<Order> = transaction {
        OrdersTable.selectAll().where { OrdersTable.userId eq userId }.map { row ->
            val orderId = row[OrdersTable.id].value
            toOrder(row, loadItems(orderId))
        }
    }

    override fun create(userId: Int, items: List<OrderItemInput>, totalAmount: Double): Order = transaction {
        val orderId = OrdersTable.insertAndGetId {
            it[OrdersTable.userId] = userId
            it[OrdersTable.status] = OrderStatus.PENDING
            it[OrdersTable.totalAmount] = BigDecimal.valueOf(totalAmount)
        }

        items.forEach { input ->
            val priceRow = ProductsTable.selectAll().where { ProductsTable.id eq input.productId }.single()
            val price = priceRow[ProductsTable.price]
            OrderItemsTable.insertAndGetId {
                it[OrderItemsTable.orderId] = orderId
                it[OrderItemsTable.productId] = input.productId
                it[OrderItemsTable.quantity] = input.quantity
                it[OrderItemsTable.priceAtTime] = price
            }
        }

        val orderRow = OrdersTable.selectAll().where { OrdersTable.id eq orderId }.single()
        toOrder(orderRow, loadItems(orderId.value))
    }

    override fun updateStatus(id: Int, status: OrderStatus): Boolean = transaction {
        OrdersTable.update({ OrdersTable.id eq id }) {
            it[OrdersTable.status] = status
        } > 0
    }

    override fun findAll(): List<Order> = transaction {
        OrdersTable.selectAll().map { row ->
            val orderId = row[OrdersTable.id].value
            toOrder(row, loadItems(orderId))
        }
    }

    override fun countByStatus(status: OrderStatus): Long = transaction {
        OrdersTable.selectAll().where { OrdersTable.status eq status }.count()
    }

    override fun totalRevenue(): Double = transaction {
        OrdersTable.selectAll()
            .where { OrdersTable.status eq OrderStatus.CONFIRMED }
            .sumOf { it[OrdersTable.totalAmount].toDouble() }
    }
}
