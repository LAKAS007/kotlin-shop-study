package com.kotlinshop.domain.models

data class AuditLog(
    val id: Int,
    val userId: Int?,
    val action: String,
    val entityType: String,
    val entityId: Int?,
    val details: String?,
    val createdAt: Long
)
