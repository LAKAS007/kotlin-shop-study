package com.kotlinshop.infrastructure.messaging

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object RabbitMQClient {
    const val ORDER_QUEUE = "order_events"
    private lateinit var connection: Connection

    fun init(host: String, port: Int, username: String, password: String) {
        val factory = ConnectionFactory().apply {
            this.host = host
            this.port = port
            this.username = username
            this.password = password
        }
        connection = factory.newConnection()
    }

    fun createChannel() = connection.createChannel().also { ch ->
        ch.queueDeclare(ORDER_QUEUE, true, false, false, null)
    }

    fun close() = connection.close()
}
