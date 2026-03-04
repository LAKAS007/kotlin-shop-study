package com.kotlinshop.infrastructure.db.repositories

import com.kotlinshop.domain.models.User
import com.kotlinshop.domain.models.UserRole
import com.kotlinshop.domain.repositories.UserRepository
import com.kotlinshop.infrastructure.db.tables.UsersTable
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    private fun toUser(row: ResultRow): User = User(
        id = row[UsersTable.id].value,
        username = row[UsersTable.username],
        email = row[UsersTable.email],
        passwordHash = row[UsersTable.passwordHash],
        role = row[UsersTable.role],
        createdAt = row[UsersTable.createdAt]
            .toJavaLocalDateTime()
            .toEpochSecond(java.time.ZoneOffset.UTC)
    )

    override fun findById(id: Int): User? = transaction {
        UsersTable.selectAll().where { UsersTable.id eq id }.singleOrNull()?.let { toUser(it) }
    }

    override fun findByEmail(email: String): User? = transaction {
        UsersTable.selectAll().where { UsersTable.email eq email }.singleOrNull()?.let { toUser(it) }
    }

    override fun findByUsername(username: String): User? = transaction {
        UsersTable.selectAll().where { UsersTable.username eq username }.singleOrNull()?.let { toUser(it) }
    }

    override fun create(username: String, email: String, passwordHash: String, role: UserRole): User = transaction {
        val id = UsersTable.insertAndGetId {
            it[UsersTable.username] = username
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.role] = role
        }
        UsersTable.selectAll().where { UsersTable.id eq id }.single().let { toUser(it) }
    }

    override fun existsByEmail(email: String): Boolean = transaction {
        UsersTable.selectAll().where { UsersTable.email eq email }.count() > 0
    }

    override fun existsByUsername(username: String): Boolean = transaction {
        UsersTable.selectAll().where { UsersTable.username eq username }.count() > 0
    }

    override fun countAll(): Long = transaction {
        UsersTable.selectAll().count()
    }
}
