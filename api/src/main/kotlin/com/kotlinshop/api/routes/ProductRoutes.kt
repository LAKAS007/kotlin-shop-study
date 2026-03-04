package com.kotlinshop.api.routes

import com.kotlinshop.api.dto.CreateProductRequest
import com.kotlinshop.api.dto.ProductResponse
import com.kotlinshop.api.dto.UpdateProductRequest
import com.kotlinshop.domain.exceptions.ForbiddenException
import com.kotlinshop.services.ProductService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private fun com.kotlinshop.domain.models.Product.toResponse() =
    ProductResponse(id, name, description, price, stock, createdAt, updatedAt)

fun Route.productRoutes(productService: ProductService) {
    get("/products") {
        call.respond(HttpStatusCode.OK, productService.getAll().map { it.toResponse() })
    }
    get("/products/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(HttpStatusCode.OK, productService.getById(id).toResponse())
    }
}

fun Route.adminProductRoutes(productService: ProductService) {
    post("/products") {
        val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
        if (role != "ADMIN") throw ForbiddenException()
        val req = call.receive<CreateProductRequest>()
        val product = productService.create(req.name, req.description, req.price, req.stock)
        call.respond(HttpStatusCode.Created, product.toResponse())
    }
    put("/products/{id}") {
        val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
        if (role != "ADMIN") throw ForbiddenException()
        val id = call.parameters["id"]!!.toInt()
        val req = call.receive<UpdateProductRequest>()
        val product = productService.update(id, req.name, req.description, req.price, req.stock)
        call.respond(HttpStatusCode.OK, product.toResponse())
    }
    delete("/products/{id}") {
        val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
        if (role != "ADMIN") throw ForbiddenException()
        val id = call.parameters["id"]!!.toInt()
        productService.delete(id)
        call.respond(HttpStatusCode.NoContent)
    }
}
