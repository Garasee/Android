package com.example.garasee.data.api

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("customer")
    val customer: Customer?
) {
    data class Customer(
        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("password")
        val password: String
    )
}

//data class SignupResponse(
//    @SerializedName("isSuccess")
//    val isSuccess: Boolean,

//    @SerializedName("statusCode")
//    val statusCode: Int,
//
//    @SerializedName("message")
//    val message: String,
//
//    @SerializedName("content")
//    val content: Any?

//)
//}