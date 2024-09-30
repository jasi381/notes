package com.example.routes

import com.example.auth.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    route("/$API_VERSION/users") {
        post("/register") {
            val registerRequest = try {
                call.receive<RegisterRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, "Missing some fields")
                )
            }

            try {
                val user = User(registerRequest.email, hashFunction(registerRequest.password), registerRequest.name)
                db.addUser(user)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.localizedMessage ?: "Something went wrong!"))
            }
        }

        post("/login") {
            val loginRequest = try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, "Missing some fields")
                )
            }

            try {
                val user = db.findUserWithEmail(loginRequest.email)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "User not found!"))

                if (user.hashedPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Password is incorrect!"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Something went wrong!"))
            }
        }
    }
}