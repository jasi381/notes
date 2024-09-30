package com.example.data.table

import org.jetbrains.exposed.sql.Table

object UserTable: Table() {

    val email = varchar(name = "email", length = 512 )
    val name = varchar(name = "name", length = 512 )
    val hashedPassword = varchar(name = "hashedPassword", length = 512 )


    override val primaryKey: PrimaryKey? = PrimaryKey(email)
}