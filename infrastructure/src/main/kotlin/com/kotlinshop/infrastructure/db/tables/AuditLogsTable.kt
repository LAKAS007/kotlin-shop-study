package com.kotlinshop.infrastructure.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AuditLogsTable : IntIdTable("audit_logs") {
    val userId = optReference("user_id", UsersTable)
    val action = varchar("action", 100)
    val entityType = varchar("entity_type", 50)
    val entityId = integer("entity_id").nullable()
    val details = text("details").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
