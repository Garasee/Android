package com.example.garasee.data.pref

data class ChangePasswordRequest(
    val oldPassword: String,
    val password: String,
    val confirmationPassword: String
)