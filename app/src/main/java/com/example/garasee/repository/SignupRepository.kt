package com.example.garasee.repository

import com.example.garasee.data.api.ApiService
import com.example.garasee.data.api.SignupResponse

class SignupRepository(private val apiService: ApiService) {

    suspend fun register(name: String, email: String, phone: String, password:String, confirmationPassword:String, cityId:String): SignupResponse {
        return apiService.register(name, email, phone, password, confirmationPassword, cityId)
    }

    companion object {
        @Volatile
        private var instance: SignupRepository? = null
        fun getInstance(
            apiService: ApiService
        ): SignupRepository =
            instance ?: synchronized(this) {
                instance ?: SignupRepository(apiService)
            }.also { instance = it }
    }
}