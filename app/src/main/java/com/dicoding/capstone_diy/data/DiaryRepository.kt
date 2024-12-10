package com.dicoding.capstone_diy.data

import android.util.Log
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.response.DiaryApiResponse
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DiaryRepository(private val diaryDao: DiaryDao) {

    val allDiaries: Flow<List<Diary>> = diaryDao.getAllDiaries()

    fun getFavoriteDiaries(): Flow<List<Diary>> {
        return diaryDao.getFavorites()
    }

    // Fungsi untuk mengonversi ISO 8601 menjadi timestamp (milidetik)
    private fun parseIsoDateToMillis(dateString: String): Long {
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
            .toInstant()
            .toEpochMilli()
    }


    // Get all diaries from API and save them to Room
    suspend fun fetchAllDiariesFromApi(token: String): Result<List<Diary>> {
        return try {
            val response = RetrofitInstance.apiService.getAllDiaries("Bearer $token")
            if (response.isSuccessful) {

                val responseBody = response.body()
                Log.d("API Response", "API Response Success: ${responseBody?.status_code}, Message: ${responseBody?.message}")
                responseBody?.data?.forEach {
                    Log.d("API Response", "Diary - ID: ${it.id}, Date: ${it.date}, Title: ${it.title}, Story: ${it.story}, Emotion: ${it.emotion}, Response: ${it.response}")
                }

                val apiDiaries = response.body()?.data?.map {
                    Diary(
                        id = it.id,
                        date = parseIsoDateToMillis(it.date),
                        title = it.title,
                        description = it.story,
                        emotion = it.emotion,
                        response = it.response,
                    )
                }

                // Insert only non-existing diaries
                apiDiaries?.forEach { diary ->
                    val existingDiary = diaryDao.getDiaryById(diary.id)
                    if (existingDiary == null) {
                        diaryDao.insertDiary(diary)  // Insert if not found
                    } else {
                        // Update if there are changes (like 'favorited', 'emotion', etc.)
                        if (existingDiary != diary) {
                            diaryDao.updateDiary(diary)
                        }
                    }
                }

                Result.success(apiDiaries ?: emptyList())
            } else {
                Result.failure(Exception("API error: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    suspend fun fetchDiaryById(token: String, diaryId: Int): Result<Diary> {
        return try {
            val response = RetrofitInstance.apiService.getDiaryById("Bearer $token", diaryId)
            if (response.isSuccessful) {
                val diaryFromApi = response.body()?.data?.let {
                    Diary(
                        id = it.id,
                        date = parseIsoDateToMillis(it.date), // Konversi ISO 8601 ke timestamp
                        title = it.title,
                        description = it.story,
                        emotion = it.emotion,
                        response = it.response,
                        favorited = false
                    )
                }
                Result.success(diaryFromApi ?: throw Exception("Diary not found"))
            } else {
                Result.failure(Exception("Failed to fetch diary"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createDiary(token: String, diaryRequest: DiaryRequest): Result<Diary> {
        return try {
            val response = RetrofitInstance.apiService.createDiary("Bearer $token", diaryRequest)
            if (response.isSuccessful) {
                val diaryData = response.body()?.data
                diaryData?.let {
                    val diary = Diary(
                        id = it.id,
                        date = parseIsoDateToMillis(it.date), // Konversi ISO 8601 ke timestamp
                        title = it.title,
                        description = it.story,
                        emotion = it.emotion,
                        response = it.response,
                        favorited = false
                    )
                    Result.success(diary)
                } ?: Result.failure(Exception("No diary data returned"))
            } else {
                Result.failure(Exception("API call failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDiary(token: String, diaryId: Int, diary: DiaryRequest): Result<Diary> {
        return try {
            val response = RetrofitInstance.apiService.updateDiary("Bearer $token", diaryId, diary)
            if (response.isSuccessful) {
                val updatedDiary = response.body()?.data?.let {
                    Diary(
                        id = it.id,
                        date = parseIsoDateToMillis(it.date), // Konversi ISO 8601 ke timestamp
                        title = it.title,
                        description = it.story,
                        emotion = it.emotion,
                        response = it.response,
                        favorited = false
                    )
                }
                updatedDiary?.let {
                    update(it)
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to update diary"))
            } else {
                Result.failure(Exception("Failed to update diary"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDiaryFromApi(token: String, id: Int): Result<Unit> {
        return try {
            // Menghapus diary dari API
            val response = RetrofitInstance.apiService.deleteDiary("Bearer $token", id)

            if (response.isSuccessful) {
                // Hapus diary dari local database setelah berhasil dihapus dari API
                val diaryToDelete = diaryDao.getDiaryById(id)
                diaryToDelete?.let {
                    diaryDao.deleteDiary(it)
                    Log.d("DiaryRepository", "Diary deleted from local DB")
                }

                Result.success(Unit) // Return success after both API and DB delete
            } else {
                Result.failure(Exception("Failed to delete diary from API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun insert(diary: Diary) {
        diaryDao.insertDiary(diary)
    }

    suspend fun update(diary: Diary) {
        diaryDao.updateDiary(diary)
    }

    suspend fun delete(diary: Diary) {
        diaryDao.deleteDiary(diary)
    }
}
