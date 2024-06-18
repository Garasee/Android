package com.example.garasee.data.api

import com.example.garasee.data.pref.ChangePasswordRequest
import com.example.garasee.data.pref.PostPredictRequest
import com.example.garasee.data.pref.UpdateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("auth/registration")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("confirmationPassword") confirmationPassword: String,
        @Field("cityId") cityId: String,
    ): SignupResponse

    @GET("users")
    suspend fun getUser(
    ): Response<GetUserResponse>

    @PUT("users")
    suspend fun updateUser(
        @Body updateUserRequest: UpdateUserRequest
    ): CommonResponse

    @PATCH("users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): CommonResponse

    @GET("predictions")
    suspend fun getHistory(): Response<HistoryResponse>

    @POST("predictions")
    suspend fun getPrediction(
        @Body predictRequest: PostPredictRequest
    ): Response<PredictionResponse>

}