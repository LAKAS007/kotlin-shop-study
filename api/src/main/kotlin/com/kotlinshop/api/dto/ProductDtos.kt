package com.kotlinshop.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(val name: String, val description: String, val price: Double, val stock: Int)

@Serializable
data class UpdateProductRequest(val name: String? = null, val description: String? = null, val price: Double? = null, val stock: Int? = null)

@Serializable
data class ProductResponse(val id: Int, val name: String, val description: String, val price: Double, val stock: Int, val createdAt: Long, val updatedAt: Long)
