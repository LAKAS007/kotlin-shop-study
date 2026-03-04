package com.kotlinshop.infrastructure.messaging

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

@Serializable
data class OrderEvent(
    val eventType: String,
    val orderId: Int,
    val userId: Int,
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

object OrderEventProducer {
    private val logger = LoggerFactory.getLogger(OrderEventProducer::class.java)

    fun publish(event: OrderEvent) {
        try {
            val channel = RabbitMQClient.createChannel()
            val message = Json.encodeToString(OrderEvent.serializer(), event)
            channel.basicPublish("", RabbitMQClient.ORDER_QUEUE, null, message.toByteArray())
            logger.info("Published event: $event")
            channel.close()
        } catch (e: Exception) {
            logger.error("Failed to publish event", e)
        }
    }
}
