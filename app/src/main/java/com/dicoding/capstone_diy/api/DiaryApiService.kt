package com.dicoding.capstone_diy.api

import com.dicoding.capstone_diy.data.DiaryRequest
import com.dicoding.capstone_diy.data.response.DiaryApiResponse
import com.dicoding.capstone_diy.data.response.SingleDiaryResponse
import retrofit2.Response
import retrofit2.http.*

interface DiaryApiService {

    @GET("/diary/")
    suspend fun getAllDiaries(
        @Header("Authorization") token: String,
    ): Response<DiaryApiResponse>

    @GET("/diary/{diaryId}")
    suspend fun getDiaryById(
        @Header("Authorization") token: String,
        @Path("diaryId") diaryId: Int
    ): Response<SingleDiaryResponse>

    @POST("/diary/create")
    suspend fun createDiary(
        @Header("Authorization") token: String,
        @Body diary: DiaryRequest
    ): Response<SingleDiaryResponse>

    @PUT("/diary/{diaryId}")
    suspend fun updateDiary(
        @Header("Authorization") token: String,
        @Path("diaryId") diaryId: Int,
        @Body diary: DiaryRequest
    ): Response<SingleDiaryResponse>

    @DELETE("/diary/{diaryId}")
    suspend fun deleteDiary(
        @Header("Authorization") token: String,
        @Path("diaryId") diaryId: Int
    ): Response<SingleDiaryResponse>
}
