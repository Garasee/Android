package com.example.garasee.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignupResponse




    //API GARASEE

//    @FormUrlEncoded
//    @POST("auth/login")
//    suspend fun login(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Response<LoginResponse>

//    @FormUrlEncoded
//    @POST("auth/registration")
//    suspend fun register(
//        @Field("name") name: String,
//        @Field("email") email: String,
//        @Field("phone") phone: String,
//        @Field("password") password: String,
//        @Field("confirmationPassword") confirmationPassword: String,
//        @Field("province") province: String,
//        @Field("city") city: String,
//    ): SignupResponse

//    @GET("users")
//    suspend fun getUser(
//
//    ): GetUserResponse

//    @PUT("users")
//    suspend fun updateUser(
//        @Body user: UpdateUserRequest
//    ): CommonResponse

//    @FormUrlEncoded
//    @PATCH("users/change-password")
//    suspend fun changePassword(
//        @Field("oldPassword") oldPassword: String,
//        @Field("password") password: String,
//        @Field("confirmationPassword") confirmationPassword: String
//    ): CommonResponse
//
//    @FormUrlEncoded
//    @POST("users/forgot-password")
//    suspend fun forgotPassword(
//        @Field("email") email: String
//    ): CommonResponse
//
//    @FormUrlEncoded
//    @PATCH("users/reset-password")
//    suspend fun resetPassword(
//        @Header("X-RESET-TOKEN") resetToken: String,
//        @Field("password") password: String,
//        @Field("confirmationPassword") confirmationPassword: String
//    ): CommonResponse











//    @GET("stories")
//    suspend fun getStories(
//        @Query("page") page: Int = 1,
//        @Query("size") size: Int = 20
//    ): Response<StoryResponse>
//
//    @GET("stories")
//    suspend fun getLocations(
//        @Query("page") page: Int,
//        @Query("size") size: Int
//    ): Response<StoryResponse>
//
//    @Multipart
//    @POST("stories")
//    suspend fun uploadImage(
//        @Part file: MultipartBody.Part,
//        @Part("description") description: RequestBody,
//        @Part("lat") lat: RequestBody,
//        @Part("lon") lon: RequestBody
//    ): FileUploadResponse


}