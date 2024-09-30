package com.example


import com.example.auth.JwtService
import com.example.auth.hashPassword
import com.example.plugins.*
import com.example.repository.DatabaseFactory
import com.example.repository.Repo
import io.ktor.server.application.*
import io.ktor.server.locations.KtorExperimentalLocationsAPI


fun main(args: Array<String>) {
    println("Starting server")
    io.ktor.server.netty.EngineMain.main(args)
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.module() {
    DatabaseFactory.init()

    val db = Repo()
    val jwtService = JwtService()
    val hashFunction: (String) -> String = ::hashPassword

    configureSerialization()
    configureSecurity(
        db = db,
        jwtService = jwtService,
        hashFunction = hashFunction
    )

    configureRouting(
        db = db,
        jwtService = jwtService,
        hashFunction = hashFunction

    )
}