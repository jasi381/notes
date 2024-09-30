package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.auth.JwtService
import com.example.repository.Repo
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

fun Application.configureSecurity(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {

    val jwtRealm = "NotesApp"
    authentication {
        jwt("jwt") {
            realm = jwtRealm
            verifier(jwtService.verifier)
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserWithEmail(email)
                user
            }
        }
    }
    @Serializable
    data class MySession(val count: Int = 0)

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    routing {
        get("/session/increment") {
                val session = call.sessions.get<MySession>() ?: MySession()
                call.sessions.set(session.copy(count = session.count + 1))
                call.respondText("Counter is ${session.count}. Refresh to increment.")
            }
    }
}
