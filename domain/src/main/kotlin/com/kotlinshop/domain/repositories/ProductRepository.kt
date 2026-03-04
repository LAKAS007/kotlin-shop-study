package com.kotlinshop.domain.repositories

import com.kotlinshop.domain.models.Product

interface ProductRepository {
    fun findAll(): List<Product>
    fun findById(id: Int): Product?
    fun create(name: String, description: String, price: Double, stock: Int): Product
    fun update(id: Int, name: String?, description: String?, price: Double?, stock: Int?): Product?
    fun delete(id: Int): Boolean
    fun decreaseStock(id: Int, quantity: Int): Boolean
}
