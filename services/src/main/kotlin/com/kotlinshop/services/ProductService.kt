package com.kotlinshop.services

import com.kotlinshop.domain.exceptions.ProductNotFoundException
import com.kotlinshop.domain.models.Product
import com.kotlinshop.domain.repositories.ProductRepository

class ProductService(
    private val productRepository: ProductRepository,
    private val cache: ProductCachePort? = null
) {
    fun getAll(): List<Product> {
        cache?.getAll()?.let { return it }
        val products = productRepository.findAll()
        cache?.setAll(products)
        return products
    }

    fun getById(id: Int): Product {
        cache?.get(id)?.let { return it }
        val product = productRepository.findById(id) ?: throw ProductNotFoundException(id)
        cache?.set(product)
        return product
    }

    fun create(name: String, description: String, price: Double, stock: Int): Product =
        productRepository.create(name, description, price, stock).also { cache?.invalidateAll() }

    fun update(id: Int, name: String?, description: String?, price: Double?, stock: Int?): Product {
        productRepository.findById(id) ?: throw ProductNotFoundException(id)
        return productRepository.update(id, name, description, price, stock)!!
            .also { cache?.invalidate(it.id) }
    }

    fun delete(id: Int) {
        productRepository.findById(id) ?: throw ProductNotFoundException(id)
        productRepository.delete(id)
        cache?.invalidate(id)
    }

    fun decreaseStock(productId: Int, quantity: Int): Boolean {
        val result = productRepository.decreaseStock(productId, quantity)
        if (result) cache?.invalidate(productId)
        return result
    }
}

interface ProductCachePort {
    fun get(id: Int): Product?
    fun set(product: Product)
    fun getAll(): List<Product>?
    fun setAll(products: List<Product>)
    fun invalidate(id: Int)
    fun invalidateAll()
}
