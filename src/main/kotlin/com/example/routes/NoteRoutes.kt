package com.example.routes

import com.example.auth.JwtService
import com.example.data.model.Note
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

const val API_VERSION = "v1"

fun Route.noteRoutes(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    authenticate("jwt") {
        route("/$API_VERSION/notes") {

            //for creating new note
            post("/createNote") {
                val note = try{
                    call.receive<Note>()
                }catch (e: Exception){
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, "Missing fields")
                    )
                }

                try {
                    val email = call.principal<User>()!!.email
                    db.addNote(note,email)
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(
                            true,
                            "Note Added Successfully"
                        )
                    )
                }catch (e: Exception){
                    call.respond(
                        HttpStatusCode.Conflict,
                        SimpleResponse(
                            false,"Something went wrong"
                        )
                    )
                }
            }

            //for getting all notes
            get("/getAllNotes"){
                try {
                    val email = call.principal<User>()!!.email

                    val notes = db.getAllNotes(email)
                    call.respond(HttpStatusCode.OK,notes)
                }catch (e: Exception){
                    call.respond(
                        HttpStatusCode.Conflict,
                      emptyList<Note>()
                    )
                }
            }

            //for updating a note
            post("/updateNote"){

                val note = try{
                    call.receive<Note>()
                }catch (e: Exception){
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, "Missing fields")
                    )
                }

                try {
                    val email = call.principal<User>()!!.email
                    db.updateNote(note,email)
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(
                            true,
                            "Note Updated Successfully"
                        )
                    )
                }catch (e: Exception){
                    call.respond(
                        HttpStatusCode.Conflict,
                        SimpleResponse(
                            false,"Something went wrong"
                        )
                    )
                }

            }


            //for deleting the note
            delete("/deleteNote"){
                val noteId = try{
                    call.request.queryParameters["id"]!!
                }catch (e: Exception){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false,
                            "QueryParameter: id is not present ")
                    )
                    return@delete
                }

                try {
                    val email = call.principal<User>()!!.email

                    db.deleteNote(noteId,email)
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(true,"Note Deleted Successfully")

                    )
                }catch (e: Exception){
                    call.respond(
                        HttpStatusCode.Conflict,
                        SimpleResponse(false,
                            e.message?:"Something went wrong!"
                        )

                    )
                }
            }

        }

    }
}