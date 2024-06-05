package com.example.garasee.data.api

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)

//data class LoginResponse(
//    val isSuccess: Boolean,
//    val statusCode: int,
//    val message: String,
//    val content: Content
//)
//
//data class Content(
//    val token: String
//)