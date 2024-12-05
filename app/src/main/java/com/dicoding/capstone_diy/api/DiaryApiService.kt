package com.dicoding.capstone_diy.api

import com.dicoding.capstone_diy.data.response.CreateDiaryResponse
import com.dicoding.capstone_diy.data.response.DeleteDiaryResponse
import com.dicoding.capstone_diy.data.response.EditDiaryResponse
import com.dicoding.capstone_diy.data.response.GetDiaryByIdResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DiaryApiService {

    // Get All Diaries (Get User Diary)
    @GET("diaries")
    suspend fun getAllDiaries(): Response<GetDiaryByIdResponse>

    // Create a New Diary (Create Diary)
    @POST("diaries")
    suspend fun createDiary(@Body diary: CreateDiaryResponse): Response<CreateDiaryResponse>

    // Get Diary by ID (Get Diary by Id)
    @GET("diaries/{id}")
    suspend fun getDiaryById(@Path("id") diaryId: Int): Response<GetDiaryByIdResponse>

    // Edit Diary by ID (Edit Diary by Id)
    @PUT("diaries/{id}")
    suspend fun editDiaryById(@Path("id") diaryId: Int, @Body diary: EditDiaryResponse): Response<EditDiaryResponse>

    // Delete Diary by ID (Delete Diary by Id)
    @DELETE("diaries/{id}")
    suspend fun deleteDiaryById(@Path("id") diaryId: Int): Response<DeleteDiaryResponse>
}
