package com.example.garasee.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import com.example.garasee.data.api.ApiService
import com.example.garasee.data.api.CommonResponse
import com.example.garasee.data.api.LoginResponse
import com.example.garasee.data.api.PredictionResponse
import com.example.garasee.data.pref.ChangePasswordRequest
import com.example.garasee.data.pref.PostPredictRequest
import com.example.garasee.data.pref.UpdateUserRequest
import com.example.garasee.data.pref.UserModel
import com.example.garasee.data.pref.UserPreference
import kotlinx.coroutines.flow.firstOrNull

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun changePassword(oldPassword: String, password: String, confirmationPassword: String): CommonResponse {
        val changePasswordRequest = ChangePasswordRequest(oldPassword, password, confirmationPassword)
        return apiService.changePassword(changePasswordRequest)
    }

    suspend fun updateUser(name: String, phone: String, cityId: String): CommonResponse {
        val updateUserRequest = UpdateUserRequest(name, phone, cityId)
        return apiService.updateUser(updateUserRequest)
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

    suspend fun predict(
        brand: String,
        isNew: Boolean,
        year: Number,
        engineCapacity: Float,
        peakPower: Float,
        peakTorque: Float,
        injection: String,
        length: Float,
        width: Float,
        wheelBase: Float,
        doorAmount: Int,
        seatCapacity: Int
    ): PredictionResponse {

        return try {
            val postPredictRequest = PostPredictRequest(brand, isNew, year, engineCapacity, peakPower, peakTorque, injection,
                length, width, wheelBase, doorAmount, seatCapacity)
            val response = apiService.getPrediction(postPredictRequest)
            if (response.isSuccessful) {
                response.body() ?: throw IllegalStateException("Response body is null")
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            throw e
        }
    }

    private fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return getSession().map { it.isLogin == true }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun validateToken(): Boolean {
        val token = userPreference.getToken().firstOrNull() ?: return false
        return try {
            val response = apiService.getUser()
            if (response.isSuccessful) {
                true
            } else {
                userPreference.logout()
                false
            }
        } catch (e: IOException) {
            Log.e("UserRepository", "Network error: ${e.message}")
            userPreference.logout()
            false
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.message()}")
            userPreference.logout()
            false
        }
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