package com.example.garasee.di

import android.content.Context
import android.util.Log
import com.example.garasee.data.pref.UserPreference
import com.example.garasee.data.pref.dataStore
import com.example.garasee.repository.UserRepository
import com.example.garasee.data.api.ApiConfig
import com.example.garasee.data.api.ApiService
import com.example.garasee.repository.ProfileRepository
import com.example.garasee.repository.SignupRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

object Injection {
    private suspend fun provideApiService(context: Context): ApiService {
        val pref = provideUserPreference(context)
        val token = runBlocking { pref.getToken().first() ?: "" }
        Log.d("Injection", "Token Injection: $token")
        ApiConfig.setToken(token)
        return ApiConfig.getApiService()
    }

    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }

    suspend fun provideUserRepository(context: Context): UserRepository {
        val pref = provideUserPreference(context)
        val apiService = provideApiService(context)
        return UserRepository.getInstance(pref, apiService)
    }

    suspend fun provideProfileRepository(context: Context): ProfileRepository {
        val apiService = provideApiService(context)
        return ProfileRepository.getInstance(apiService)
    }

    suspend fun provideSignupRepository(context: Context): SignupRepository {
        val apiService = provideApiService(context)
        return SignupRepository.getInstance(apiService)
    }

}
