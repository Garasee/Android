package com.example.garasee.data.api

data class LoginResponse(
    val isSuccess: Boolean,
    val statusCode: Int,
    val message: String,
    val content: Content
)

data class Content(
    val token: String
)