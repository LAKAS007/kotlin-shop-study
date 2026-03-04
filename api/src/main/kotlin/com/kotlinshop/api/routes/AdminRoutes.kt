package com.kotlinshop.api.routes

import com.kotlinshop.api.dto.StatsResponse
import com.kotlinshop.domain.exceptions.ForbiddenException
import com.kotlinshop.services.AdminService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes(adminService: AdminService) {
    get("/stats/orders") {
        val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
        if (role != "ADMIN") throw ForbiddenException()
        val stats = adminService.getStats()
        call.respond(HttpStatusCode.OK, StatsResponse(
            totalOrders = stats.totalOrders,
            pendingOrders = stats.pendingOrders,
            confirmedOrders = stats.confirmedOrders,
            cancelledOrders = stats.cancelledOrders,
            totalRevenue = stats.totalRevenue,
            totalProducts = stats.totalProducts,
            totalUsers = stats.totalUsers
        ))
    }
}
