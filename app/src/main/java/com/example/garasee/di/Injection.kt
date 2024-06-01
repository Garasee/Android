package com.example.garasee.di

import android.app.Application
import android.content.Context
import com.example.garasee.data.pref.UserPreference
import com.example.garasee.data.pref.dataStore
import com.example.garasee.repository.UserRepository
import com.example.garasee.data.api.ApiConfig
import com.example.garasee.data.api.ApiService
import com.example.garasee.repository.NoteRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

object Injection {
    private fun provideApiService(context: Context): ApiService {
        val pref = provideUserPreference(context)
        val token = runBlocking { pref.getToken().first() ?: "" }
        return ApiConfig.getApiService(token)
    }

    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = provideUserPreference(context)
        val apiService = provideApiService(context)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideNoteRepository(application: Application): NoteRepository {
        return NoteRepository.getInstance(application)
    }
}
