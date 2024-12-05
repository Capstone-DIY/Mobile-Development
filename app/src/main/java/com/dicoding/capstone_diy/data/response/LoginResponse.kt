package com.dicoding.capstone_diy.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("status_code")
    val statusCode: Int,

    @field:SerializedName("data")
    val data: LoginData,

    @field:SerializedName("message")
    val message: String
)

data class LoginData(

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("email")
    val email: String
)