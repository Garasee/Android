package com.example.garasee.repository

import com.example.garasee.data.api.ApiConfig
import com.example.garasee.data.api.ApiService
import com.example.garasee.data.api.SignupResponse

class SignupRepository(token: String) {
    private val apiService: ApiService = ApiConfig.getApiService(token)

    suspend fun register(name: String, email: String, password: String): SignupResponse {
        return apiService.register(name, email, password)
    }
}