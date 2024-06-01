package com.example.garasee.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import com.example.garasee.data.api.ApiService
import com.example.garasee.data.api.LoginResponse
import com.example.garasee.data.pref.UserModel
import com.example.garasee.data.pref.UserPreference

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(email, password)
            if (response.isSuccessful) {
                response.body() ?: throw IllegalStateException("Response body is null")
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            throw e
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return getSession().map { it.isLogin }
    }

    suspend fun logout(token: String) {
        Log.d("UserRepository", "Logging out for token: $token")
        userPreference.logout(token)
        Log.d("UserRepository", "Session and token removed for token: $token")
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}