package com.example.garasee.data.api

import com.google.gson.annotations.SerializedName
data class SignupResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("statusCode")
    val statusCode: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("content")
    val content: Any?

)
