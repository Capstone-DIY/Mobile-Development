package com.dicoding.capstone_diy.api

import com.dicoding.capstone_diy.data.response.LoginResponse
import com.dicoding.capstone_diy.data.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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

    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): Response<RegisterResponse>
}