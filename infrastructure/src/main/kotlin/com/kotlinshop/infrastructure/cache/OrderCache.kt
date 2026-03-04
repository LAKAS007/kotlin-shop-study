package com.kotlinshop.infrastructure.cache

object OrderCache {
    private const val TTL_SECONDS = 300
    private const val KEY_PREFIX = "order:"

    fun get(id: Int): String? = RedisClient.use { it.get("$KEY_PREFIX$id") }

    fun set(id: Int, json: String) = RedisClient.use {
        it.setex("$KEY_PREFIX$id", TTL_SECONDS.toLong(), json)
    }

    fun invalidate(id: Int) = RedisClient.use { it.del("$KEY_PREFIX$id") }
}
