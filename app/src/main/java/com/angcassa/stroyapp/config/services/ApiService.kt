package com.angcassa.stroyapp.config.services

import com.angcassa.stroyapp.config.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<RegisResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>

    @Multipart
    @POST("stories")
    fun uploadGps(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat")lat: Double,
        @Part("lon")lon: Double
    ): Call<UploadResponse>

    @GET("stories?location=1")
    fun MapStories(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getStoryDB(
        @Header("Authorization") token: String,
        @Query("page")page:Int,
        @Query("size")size:Int
    ): StoryResponse
}