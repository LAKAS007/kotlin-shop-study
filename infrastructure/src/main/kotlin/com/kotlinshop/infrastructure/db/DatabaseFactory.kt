package com.kotlinshop.infrastructure.db

import com.kotlinshop.infrastructure.db.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(jdbcUrl: String, username: String, password: String) {
        Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .locations("classpath:db/migration")
            .load()
            .migrate()

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
        }
        Database.connect(HikariDataSource(config))
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) { transaction { block() } }
}
