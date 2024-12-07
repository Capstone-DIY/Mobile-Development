package com.dicoding.capstone_diy.api

import com.dicoding.capstone_diy.data.response.LoginResponse
import com.dicoding.capstone_diy.data.response.ProfileResponse
import com.dicoding.capstone_diy.data.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    @POST("register")
    suspend fun registerUser(
        @Body user: Map<String, String>
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(
        @Body user: Map<String, String>
    ): Response<LoginResponse>

    @GET("user")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @PATCH("user/edit")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body profileData: Map<String, String>
    ): Response<ProfileResponse>

}