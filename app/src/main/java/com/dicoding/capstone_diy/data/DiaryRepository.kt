package com.dicoding.capstone_diy.data

import android.util.Log
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.response.QuoteResponse
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class DiaryRepository(private val diaryDao: DiaryDao) {

    val allDiaries: Flow<List<Diary>> = diaryDao.getAllDiaries()

    fun getFavoriteDiaries(): Flow<List<Diary>> {
        return diaryDao.getFavorites()
    }

    private fun parseIsoDateToMillis(dateString: String): Long {
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
            .toInstant()
            .toEpochMilli()
    }

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

                apiDiaries?.forEach { diary ->
                    val existingDiary = diaryDao.getDiaryById(diary.id)
                    if (existingDiary == null) {
                        diaryDao.insertDiary(diary)
                    } else {
                        if (existingDiary != diary) {
                            diaryDao.updateDiary(diary)
                        }
                    }
                }

                Result.success(apiDiaries ?: emptyList())
            } else {
                if (response.code() == 403) {
                    throw Exception("Token expired")
                } else {
                    throw Exception("API error: ${response.errorBody()?.string()}")
                }
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
                        date = parseIsoDateToMillis(it.date),
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
                        date = parseIsoDateToMillis(it.date),
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
                        date = parseIsoDateToMillis(it.date),
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

    suspend fun getEmotionStatisticsForLastWeek(): Map<String, Int> {
        val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
        val currentDate = System.currentTimeMillis()

        val diaryList = diaryDao.getDiaryBetweenDates(oneWeekAgo, currentDate)
        if (diaryList.isEmpty()) return emptyMap()

        val emotionFrequency = mutableMapOf<String, Int>()
        diaryList.forEach { diary ->
            val emotion = diary.emotion ?: "Neutral"
            emotionFrequency[emotion] = (emotionFrequency[emotion] ?: 0) + 1
        }

        return emotionFrequency
    }

    suspend fun getEmotionStatisticsByDay(): Map<String, Map<String, Int>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val lastWeekDate = calendar.timeInMillis
        val diaryEntries = diaryDao.getLastWeekEntries(lastWeekDate)
        Log.d("EmotionStatistics", "Diary entries fetched: $diaryEntries")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val groupedByDay = diaryEntries.groupBy { entry ->
            dateFormatter.format(entry.date)
        }
        Log.d("EmotionStatistics", "Grouped by day: ${groupedByDay.map { it.key to it.value.map { entry -> entry.id } }}")

        val result = groupedByDay.mapValues { (_, entries) ->
            entries.groupingBy { it.emotion.orEmpty() }.eachCount()
        }
        Log.d("EmotionStatistics", "Result: ${result.map { it.key to it.value }}")
        return result
    }



    suspend fun deleteDiaryFromApi(token: String, id: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.apiService.deleteDiary("Bearer $token", id)

            if (response.isSuccessful) {
                val diaryToDelete = diaryDao.getDiaryById(id)
                diaryToDelete?.let {
                    diaryDao.deleteDiary(it)
                    Log.d("DiaryRepository", "Diary deleted from local DB")
                }

                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete diary from API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuote(token: String, dominantEmotion: String): QuoteResponse? {
        Log.d("APIRequest", "Token: $token, Dominant Emotion: $dominantEmotion")

        try {
            val response = RetrofitInstance.apiService.getQuote("Bearer $token", dominantEmotion)

            if (response.isSuccessful) {
                Log.d("APIResponse", "Response body: ${response.body()}")
                return response.body() // Pastikan response.body() berisi data yang benar
            } else {
                Log.e("APIResponse", "Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("APIResponse", "Exception: ${e.message}")
        }
        return null
    }

    suspend fun deleteAllDiaries(): Boolean {
        return try {
            diaryDao.deleteAll()
            true
        } catch (e: Exception) {
            Log.e("DiaryRepository", "Failed to delete all diaries: ${e.message}")
            false
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