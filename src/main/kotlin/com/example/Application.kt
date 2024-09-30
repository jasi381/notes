package com.example


import com.example.plugins.*
import com.example.repository.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.Locations

fun main(args: Array<String>) {
    println("Starting server")
    io.ktor.server.netty.EngineMain.main(args)
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.module() {
    println("Initializing application module")
    DatabaseFactory.init()
    install(Locations)

    configureSerialization()
    configureSecurity()
    configureRouting()
    println("Application module initialization complete")
}