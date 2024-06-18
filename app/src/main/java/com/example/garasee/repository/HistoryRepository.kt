package com.example.garasee.repository

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import com.example.garasee.data.api.ApiService
import com.example.garasee.data.api.ContentItem
import com.example.garasee.data.pref.UserPreference
import kotlinx.coroutines.flow.first

class HistoryRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun getHistory(): List<ContentItem> {
        val token = userPreference.getToken().first() ?: ""
        return try {
            val response = apiService.getHistory()
            if (response.isSuccessful) {
                response.body()?.content?.map {
                    ContentItem(
                        createdAt = it.createdAt,
                        isAcceptable = it.isAcceptable,
                        price = it.price,
                        brand = it.brand
                    )
                } ?: emptyList()
            } else {
                Log.e("HistoryRepository", "Failed to fetch history: ${response.message()}")
                emptyList()
            }
        } catch (e: IOException) {
            Log.e("HistoryRepository", "Network error: ${e.message}")
            emptyList()
        } catch (e: HttpException) {
            Log.e("HistoryRepository", "HTTP error: ${e.message()}")
            emptyList()
        }
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): HistoryRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
