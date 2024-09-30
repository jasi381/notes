package com.example.repository

import com.example.data.model.Note
import com.example.data.model.User
import com.example.data.table.NotesTable
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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
            .mapNotNull { toUser(it) }
            .singleOrNull()
    }

    private fun toUser(row: ResultRow?): User? {

        return if(row == null) {
            null
        } else User(
            email = row[UserTable.email],
            userName = row[UserTable.name],
            hashedPassword = row[UserTable.hashedPassword]
        )
    }


    //======================== NOTES ========================

    suspend fun addNote(note: Note,email: String){

        dbQuery {
            NotesTable.insert { nt->
                nt[id] = note.id
                nt[noteTitle] = note.noteTitle
                nt[description] = note.description
                nt[userEmail] = email
                nt[date] = note.date

            }
        }

    }

    suspend fun getAllNotes(email: String):List<Note> = dbQuery{
        NotesTable.selectAll()
            .where{NotesTable.userEmail eq email}
            .mapNotNull { toNote(it) }
    }

    suspend fun updateNote(note: Note,email: String){
        dbQuery {
            NotesTable.update(
                where = {
                    (NotesTable.userEmail eq email) and  (NotesTable.id eq note.id)
                }
            ){nt ->
                nt[noteTitle] = note.noteTitle
                nt[description] = note.description
                nt[date] = note.date

            }
        }
    }


    suspend fun deleteNote(id: String,email: String) {
        dbQuery {
            NotesTable.deleteWhere {
               ( userEmail.eq(email)) and
               ( NotesTable.id eq id)
            }
        }
    }

    private fun toNote(row: ResultRow?): Note?{
        return if(row == null){
            null
        }else{
            Note(
                id = row[NotesTable.id],
                noteTitle = row[NotesTable.noteTitle],
                description = row[NotesTable.description],
                date = row[NotesTable.date]

            )
        }

    }
}
