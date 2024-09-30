package com.example.repository

import com.example.data.table.NotesTable
import com.example.data.table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {

    fun init(){
        Database.connect(hikari())
        transaction{
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NotesTable)
        }
    }

    fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        config.jdbcUrl = "jdbc:postgresql://dpg-crt7o4d6l47c73d7uh0g-a.oregon-postgres.render.com:5432/notes_2auc"

        config. validate()
        return HikariDataSource(config)
    }


    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}
