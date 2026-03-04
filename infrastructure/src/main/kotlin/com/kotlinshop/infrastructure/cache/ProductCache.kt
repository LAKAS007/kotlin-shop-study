package com.kotlinshop.infrastructure.cache

object ProductCache {
    private const val TTL_SECONDS = 300
    private const val KEY_PREFIX = "product:"
    private const val ALL_KEY = "products:all"

    fun get(id: Int): String? = RedisClient.use { it.get("$KEY_PREFIX$id") }

    fun set(id: Int, json: String) = RedisClient.use {
        it.setex("$KEY_PREFIX$id", TTL_SECONDS.toLong(), json)
    }

    fun getAll(): String? = RedisClient.use { it.get(ALL_KEY) }

    fun setAll(json: String) = RedisClient.use {
        it.setex(ALL_KEY, TTL_SECONDS.toLong(), json)
    }

    fun invalidate(id: Int) = RedisClient.use {
        it.del("$KEY_PREFIX$id")
        it.del(ALL_KEY)
    }

    fun invalidateAll() = RedisClient.use { it.del(ALL_KEY) }
}
