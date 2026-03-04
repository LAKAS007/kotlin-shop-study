package com.kotlinshop.infrastructure.db.repositories

import com.kotlinshop.domain.models.Product
import com.kotlinshop.domain.repositories.ProductRepository
import com.kotlinshop.infrastructure.db.tables.ProductsTable
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.time.ZoneOffset

class ProductRepositoryImpl : ProductRepository {

    private fun toProduct(row: ResultRow): Product = Product(
        id = row[ProductsTable.id].value,
        name = row[ProductsTable.name],
        description = row[ProductsTable.description],
        price = row[ProductsTable.price].toDouble(),
        stock = row[ProductsTable.stock],
        createdAt = row[ProductsTable.createdAt].toJavaLocalDateTime().toEpochSecond(ZoneOffset.UTC),
        updatedAt = row[ProductsTable.updatedAt].toJavaLocalDateTime().toEpochSecond(ZoneOffset.UTC)
    )

    override fun findAll(): List<Product> = transaction {
        ProductsTable.selectAll().map { toProduct(it) }
    }

    override fun findById(id: Int): Product? = transaction {
        ProductsTable.selectAll().where { ProductsTable.id eq id }.singleOrNull()?.let { toProduct(it) }
    }

    override fun create(name: String, description: String, price: Double, stock: Int): Product = transaction {
        val id = ProductsTable.insertAndGetId {
            it[ProductsTable.name] = name
            it[ProductsTable.description] = description
            it[ProductsTable.price] = BigDecimal.valueOf(price)
            it[ProductsTable.stock] = stock
        }
        ProductsTable.selectAll().where { ProductsTable.id eq id }.single().let { toProduct(it) }
    }

    override fun update(id: Int, name: String?, description: String?, price: Double?, stock: Int?): Product? = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val updated = ProductsTable.update({ ProductsTable.id eq id }) { row ->
            name?.let { row[ProductsTable.name] = it }
            description?.let { row[ProductsTable.description] = it }
            price?.let { row[ProductsTable.price] = BigDecimal.valueOf(it) }
            stock?.let { row[ProductsTable.stock] = it }
            row[ProductsTable.updatedAt] = now
        }
        if (updated == 0) null
        else ProductsTable.selectAll().where { ProductsTable.id eq id }.singleOrNull()?.let { toProduct(it) }
    }

    override fun delete(id: Int): Boolean = transaction {
        ProductsTable.deleteWhere { ProductsTable.id eq id } > 0
    }

    override fun decreaseStock(id: Int, quantity: Int): Boolean = transaction {
        val row = ProductsTable.selectAll().where { ProductsTable.id eq id }.singleOrNull()
            ?: return@transaction false
        val currentStock = row[ProductsTable.stock]
        if (quantity > 0 && currentStock < quantity) return@transaction false
        ProductsTable.update({ ProductsTable.id eq id }) {
            it[ProductsTable.stock] = currentStock - quantity
        }
        true
    }
}
