package com.kotlinshop.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val createdAt: Long,
    val updatedAt: Long
)
