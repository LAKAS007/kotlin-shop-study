package com.kotlinshop.infrastructure.db.tables

import com.kotlinshop.domain.models.UserRole
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = enumerationByName("role", 10, UserRole::class)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
