package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.User
import jdk.javadoc.internal.doclint.Env

class JwtService {
    private val issuer = "noteServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)


    val verifier : JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String{
        return JWT.create()
            .withSubject("NotesAuth")
            .withIssuer(issuer)
            .withClaim("email",user.email)
            .sign(algorithm)

    }
}