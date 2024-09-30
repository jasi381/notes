package com.example.repository

import com.example.data.model.User
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.*

class Repo {
    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { ut ->
                ut[email] = user.email
                ut[name] = user.userName
                ut[hashedPassword] = user.hashedPassword
            }
        }
    }

    suspend fun findUserWithEmail(email: String): User? = dbQuery {
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .map { toUser(it) }
            .singleOrNull()
    }

    private fun toUser(row: ResultRow): User =
        User(
            email = row[UserTable.email],
            userName = row[UserTable.name],
            hashedPassword = row[UserTable.hashedPassword]
        )
}