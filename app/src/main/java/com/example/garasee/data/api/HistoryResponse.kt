package com.example.garasee.data.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class HistoryResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("content")
	val content: List<ContentItem>,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean
)

@Parcelize
data class ContentItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("isAcceptable")
	val isAcceptable: Boolean,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("brand")
	val brand: String
) : Parcelable
