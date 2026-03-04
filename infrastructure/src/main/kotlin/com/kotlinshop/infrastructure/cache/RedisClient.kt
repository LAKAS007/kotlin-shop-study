package com.kotlinshop.infrastructure.cache

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object RedisClient {
    private lateinit var pool: JedisPool

    fun init(host: String, port: Int, password: String? = null) {
        pool = JedisPool(JedisPoolConfig(), host, port, 2000, password)
    }

    fun <T> use(block: (redis.clients.jedis.Jedis) -> T): T =
        pool.resource.use { block(it) }

    fun close() = pool.close()
}
