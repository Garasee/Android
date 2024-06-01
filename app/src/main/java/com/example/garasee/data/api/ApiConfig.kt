package com.example.garasee.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.garasee.BuildConfig

object ApiConfig {
    const val BASE_URL = BuildConfig.BASE_URL

    fun getApiService(token: String): ApiService {
        val loggingInterceptor = if(BuildConfig.DEBUG) { HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY) }else { HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.NONE) }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
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