package com.example.plugins

import com.example.auth.JwtService
import com.example.data.model.User
import com.example.repository.Repo
import com.example.routes.noteRoutes
import com.example.routes.userRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.configureRouting(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {



    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("/notes") {
            get("/{id}") {
                val id = call.parameters["id"]
                call.respond(id ?:"")
            }

            get {
                val id = call.request.queryParameters["id"]
                call.respond(id ?: "")
            }

            post("/create") {
                val body = call.receive<String>()
                call.respond(body)
            }

            delete {
                val body = call.receive<String>()
                call.respond(body)
            }
        }

        get("/token") {
            val email = call.request.queryParameters["email"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing email")
            val password = call.request.queryParameters["password"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing password")
            val username = call.request.queryParameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing username")

            val user = User(email, hashFunction(password), username)
            call.respond(jwtService.generateToken(user))
        }

        userRoutes(db, jwtService, hashFunction)

        noteRoutes(db)

        // For All Unhandled routes
        route("{...}") {
            handle {
                call.respondText("404: Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}