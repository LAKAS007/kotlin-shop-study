package com.kotlinshop.domain.repositories

import com.kotlinshop.domain.models.AuditLog

interface AuditLogRepository {
    fun create(userId: Int?, action: String, entityType: String, entityId: Int?, details: String?): AuditLog
    fun findAll(): List<AuditLog>
}
