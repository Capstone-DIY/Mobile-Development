package com.dicoding.capstone_diy.data.response

import com.dicoding.capstone_diy.data.Diary
import com.google.gson.annotations.SerializedName

data class GetUserDiaryResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Diary>
)