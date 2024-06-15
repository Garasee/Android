package com.example.garasee.repository

import android.util.Log
import com.example.garasee.data.api.ApiService
import com.example.garasee.data.api.GetUserResponse
import retrofit2.HttpException
import java.io.IOException

class ProfileRepository private constructor(
    private val apiService: ApiService
) {

    suspend fun getUser(): GetUserResponse? {
        return try {
            val response = apiService.getUser()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ProfileRepository", "Failed to fetch user: ${response.message()}")
                null
            }
        } catch (e: IOException) {
            Log.e("ProfileRepository", "Network error: ${e.message}")
            null
        } catch (e: HttpException) {
            Log.e("ProfileRepository", "HTTP error: ${e.message()}")
            null
        }
    }

    companion object {
        @Volatile
        private var instance: ProfileRepository? = null

        fun getInstance(
            apiService: ApiService
        ): ProfileRepository =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(apiService)
            }.also { instance = it }
    }
}