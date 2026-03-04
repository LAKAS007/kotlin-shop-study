package com.kotlinshop.infrastructure.cache

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object RedisClient {
    private var pool: JedisPool? = null

    fun init(host: String, port: Int, password: String? = null) {
        pool = JedisPool(JedisPoolConfig(), host, port, 2000, password)
    }

    fun <T> use(block: (redis.clients.jedis.Jedis) -> T): T? {
        return try {
            pool?.resource?.use(block)
        } catch (e: Exception) {
            null
        }
    }

    fun close() = pool?.close()
}
