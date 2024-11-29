package com.dicoding.capstone_diy.data.response

import com.google.gson.annotations.SerializedName

data class CreateDiaryResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CreatedDiaryData
)

data class CreatedDiaryData(
    @SerializedName("diaryId") val diaryId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("story") val story: String
)