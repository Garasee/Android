package com.example.garasee.data.api

import com.google.gson.annotations.SerializedName

data class GetCityResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("content")
	val content: Cities,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean
)

data class Cities(

	@field:SerializedName("cities")
	val cities: List<CitiesItem>
)

data class CitiesItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
