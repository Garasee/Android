package com.example.garasee.data.api

data class GetUserResponse(
	val code: Int,
	val message: String,
	val content: Result,
	val isSuccess: Boolean
)

data class Result(
	val user: User
)

data class City(
	val name: String,
	val id: String
)

data class User(
	val phone: String,
	val city: City,
	val name: String,
	val id: String,
	val email: String
)
