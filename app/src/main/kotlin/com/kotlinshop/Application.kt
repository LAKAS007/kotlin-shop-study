package com.kotlinshop

import com.kotlinshop.api.plugins.configureAuthentication
import com.kotlinshop.api.plugins.configureSerialization
import com.kotlinshop.api.plugins.configureStatusPages
import com.kotlinshop.api.configureRouting
import com.kotlinshop.domain.models.Product
import com.kotlinshop.domain.models.Order
import com.kotlinshop.infrastructure.cache.OrderCache
import com.kotlinshop.infrastructure.cache.ProductCache
import com.kotlinshop.infrastructure.cache.RedisClient
import com.kotlinshop.infrastructure.db.DatabaseFactory
import com.kotlinshop.infrastructure.db.repositories.AuditLogRepositoryImpl
import com.kotlinshop.infrastructure.db.repositories.OrderRepositoryImpl
import com.kotlinshop.infrastructure.db.repositories.ProductRepositoryImpl
import com.kotlinshop.infrastructure.db.repositories.UserRepositoryImpl
import com.kotlinshop.infrastructure.messaging.OrderEventConsumer
import com.kotlinshop.infrastructure.messaging.OrderEventProducer
import com.kotlinshop.infrastructure.messaging.OrderEvent
import com.kotlinshop.infrastructure.messaging.RabbitMQClient
import com.kotlinshop.services.AdminService
import com.kotlinshop.services.AuthService
import com.kotlinshop.services.JwtConfig
import com.kotlinshop.services.OrderCachePort
import com.kotlinshop.services.OrderEventPort
import com.kotlinshop.services.OrderService
import com.kotlinshop.services.ProductCachePort
import com.kotlinshop.services.ProductService
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val config = environment.config

    val dbUrl = config.property("database.url").getString()
    val dbUser = config.property("database.username").getString()
    val dbPassword = config.property("database.password").getString()
    DatabaseFactory.init(dbUrl, dbUser, dbPassword)

    JwtConfig.secret = config.property("jwt.secret").getString()
    JwtConfig.issuer = config.property("jwt.issuer").getString()
    JwtConfig.audience = config.property("jwt.audience").getString()

    val redisHost = config.property("redis.host").getString()
    val redisPort = config.property("redis.port").getString().toInt()
    val redisPassword = config.propertyOrNull("redis.password")?.getString()
    try {
        RedisClient.init(redisHost, redisPort, redisPassword)
    } catch (e: Exception) {
        println("Redis unavailable, caching disabled: ${e.message}")
    }

    val rabbitHost = config.property("rabbitmq.host").getString()
    val rabbitPort = config.property("rabbitmq.port").getString().toInt()
    val rabbitUser = config.property("rabbitmq.username").getString()
    val rabbitPassword = config.property("rabbitmq.password").getString()
    try {
        RabbitMQClient.init(rabbitHost, rabbitPort, rabbitUser, rabbitPassword)
        OrderEventConsumer.start()
    } catch (e: Exception) {
        println("RabbitMQ unavailable, messaging disabled: ${e.message}")
    }

    val userRepository = UserRepositoryImpl()
    val productRepository = ProductRepositoryImpl()
    val orderRepository = OrderRepositoryImpl()
    val auditLogRepository = AuditLogRepositoryImpl()

    val json = Json { ignoreUnknownKeys = true }

    val productCachePort = object : ProductCachePort {
        override fun get(id: Int): Product? =
            ProductCache.get(id)?.let { json.decodeFromString(Product.serializer(), it) }

        override fun set(product: Product) {
            ProductCache.set(product.id, json.encodeToString(Product.serializer(), product))
        }

        override fun getAll(): List<Product>? =
            ProductCache.getAll()?.let {
                json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(Product.serializer()), it)
            }

        override fun setAll(products: List<Product>) {
            ProductCache.setAll(
                json.encodeToString(kotlinx.serialization.builtins.ListSerializer(Product.serializer()), products)
            )
        }

        override fun invalidate(id: Int) { ProductCache.invalidate(id) }

        override fun invalidateAll() { ProductCache.invalidateAll() }
    }

    val orderEventPort = object : OrderEventPort {
        override fun publishOrderCreated(order: Order) =
            OrderEventProducer.publish(
                OrderEvent(
                    eventType = "ORDER_CREATED",
                    orderId = order.id,
                    userId = order.userId,
                    totalAmount = order.totalAmount
                )
            )
    }

    val orderCachePort = object : OrderCachePort {
        override fun get(id: Int): Order? =
            OrderCache.get(id)?.let { json.decodeFromString(Order.serializer(), it) }

        override fun set(order: Order) {
            OrderCache.set(order.id, json.encodeToString(Order.serializer(), order))
        }

        override fun invalidate(id: Int) { OrderCache.invalidate(id) }
    }

    val authService = AuthService(userRepository)
    val productService = ProductService(productRepository, productCachePort)
    val orderService = OrderService(orderRepository, productService, auditLogRepository, orderEventPort, orderCachePort)
    val adminService = AdminService(orderRepository, productRepository, userRepository)

    configureSerialization()
    configureAuthentication()
    configureStatusPages()
    configureRouting(authService, productService, orderService, adminService)
}
