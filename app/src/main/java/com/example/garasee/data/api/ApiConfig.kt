package com.example.garasee.data.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.garasee.BuildConfig

object ApiConfig {
    private const val BASE_URL = BuildConfig.BASE_URL

    @Volatile
    private var token: String? = null

    fun setToken(newToken: String) {
        token = newToken
    }

    fun getApiService(): ApiService {

        val loggingInterceptor = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val currentToken = token ?: throw IllegalStateException("Token is not available")
                Log.d("client", "Token client: $currentToken")
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $currentToken")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}