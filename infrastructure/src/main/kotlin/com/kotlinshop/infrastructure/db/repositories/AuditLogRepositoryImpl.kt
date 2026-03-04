package com.kotlinshop.infrastructure.db.repositories

import com.kotlinshop.domain.models.AuditLog
import com.kotlinshop.domain.repositories.AuditLogRepository
import com.kotlinshop.infrastructure.db.tables.AuditLogsTable
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneOffset

class AuditLogRepositoryImpl : AuditLogRepository {

    private fun toAuditLog(row: ResultRow): AuditLog = AuditLog(
        id = row[AuditLogsTable.id].value,
        userId = row[AuditLogsTable.userId]?.value,
        action = row[AuditLogsTable.action],
        entityType = row[AuditLogsTable.entityType],
        entityId = row[AuditLogsTable.entityId],
        details = row[AuditLogsTable.details],
        createdAt = row[AuditLogsTable.createdAt].toJavaLocalDateTime().toEpochSecond(ZoneOffset.UTC)
    )

    override fun create(userId: Int?, action: String, entityType: String, entityId: Int?, details: String?): AuditLog = transaction {
        val id = AuditLogsTable.insertAndGetId {
            it[AuditLogsTable.userId] = userId
            it[AuditLogsTable.action] = action
            it[AuditLogsTable.entityType] = entityType
            it[AuditLogsTable.entityId] = entityId
            it[AuditLogsTable.details] = details
        }
        AuditLogsTable.selectAll().where { AuditLogsTable.id eq id }.single().let { toAuditLog(it) }
    }

    override fun findAll(): List<AuditLog> = transaction {
        AuditLogsTable.selectAll().map { toAuditLog(it) }
    }
}
