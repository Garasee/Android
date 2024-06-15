package com.example.garasee.data.api

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: Any? = null,

	@field:SerializedName("content")
	val content: Any? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)
