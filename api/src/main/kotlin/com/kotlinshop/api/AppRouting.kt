package com.kotlinshop.api

import com.kotlinshop.api.routes.adminProductRoutes
import com.kotlinshop.api.routes.adminRoutes
import com.kotlinshop.api.routes.authRoutes
import com.kotlinshop.api.routes.orderRoutes
import com.kotlinshop.api.routes.productRoutes
import com.kotlinshop.services.AdminService
import com.kotlinshop.services.AuthService
import com.kotlinshop.services.OrderService
import com.kotlinshop.services.ProductService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.swagger.*

fun Application.configureRouting(
    authService: AuthService,
    productService: ProductService,
    orderService: OrderService,
    adminService: AdminService
) {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        authRoutes(authService)
        productRoutes(productService)
        authenticate("auth-jwt") {
            adminProductRoutes(productService)
            orderRoutes(orderService)
            adminRoutes(adminService)
        }
    }
}
