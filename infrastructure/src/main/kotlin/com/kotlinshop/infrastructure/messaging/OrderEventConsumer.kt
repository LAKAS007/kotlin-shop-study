package com.kotlinshop.infrastructure.messaging

import com.rabbitmq.client.DeliverCallback
import org.slf4j.LoggerFactory

object OrderEventConsumer {
    private val logger = LoggerFactory.getLogger(OrderEventConsumer::class.java)

    fun start() {
        Thread {
            try {
                val channel = RabbitMQClient.createChannel()
                val deliverCallback = DeliverCallback { _, delivery ->
                    try {
                        val message = String(delivery.body)
                        logger.info("[Worker] Received order event: $message")
                        logger.info("[Worker] Sending email notification (stub) for event: $message")
                        channel.basicAck(delivery.envelope.deliveryTag, false)
                    } catch (e: Exception) {
                        logger.error("[Worker] Failed to process message", e)
                    }
                }
                channel.basicConsume(RabbitMQClient.ORDER_QUEUE, false, deliverCallback) { _ -> }
                logger.info("[Worker] Started consuming from ${RabbitMQClient.ORDER_QUEUE}")
            } catch (e: Exception) {
                logger.error("[Worker] Failed to start consumer", e)
            }
        }.also { it.isDaemon = true; it.start() }
    }
}
