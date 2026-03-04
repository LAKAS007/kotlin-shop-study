package com.kotlinshop.domain.exceptions

sealed class DomainException(message: String) : RuntimeException(message)
class ProductNotFoundException(id: Int) : DomainException("Product $id not found")
class ProductOutOfStockException(id: Int, available: Int) : DomainException("Product $id out of stock (available: $available)")
class OrderNotFoundException(id: Int) : DomainException("Order $id not found")
class UserNotFoundException(id: Int) : DomainException("User $id not found")
class EmailAlreadyExistsException(email: String) : DomainException("Email $email already registered")
class UsernameAlreadyExistsException(username: String) : DomainException("Username $username taken")
class UnauthorizedException(message: String = "Unauthorized") : DomainException(message)
class ForbiddenException(message: String = "Forbidden") : DomainException(message)
class OrderCancellationException(message: String) : DomainException(message)
