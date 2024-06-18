package com.example.garasee.data.api

data class PredictionResponse(
	val isSuccess: Boolean,
	val code: Int,
	val message: String,
	val content: PredictionContent

)

data class PredictionContent(
	val isAcceptable: Boolean,
	val price: Any
)
