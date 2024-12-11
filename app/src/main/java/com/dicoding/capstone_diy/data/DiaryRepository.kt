package com.dicoding.capstone_diy.data

import android.util.Log
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.response.DiaryApiResponse
import com.dicoding.capstone_diy.data.response.QuoteResponse
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

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

    // Tambahkan fungsi baru untuk menghitung statistik emosi
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
        // Hitung tanggal satu minggu yang lalu
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7) // Mengurangi 7 hari
        val lastWeekDate = calendar.timeInMillis // Dapatkan waktu dalam milidetik

        // Ambil data dari database dengan lastWeekDate sebagai parameter
        val diaryEntries = diaryDao.getLastWeekEntries(lastWeekDate)

        // Log data yang diambil dari database
        Log.d("EmotionStatistics", "Diary entries fetched: $diaryEntries")

        // Format tanggal untuk grouping
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Kelompokkan berdasarkan hari dan hitung jumlah per tag emosi
        val groupedByDay = diaryEntries.groupBy { entry ->
            dateFormatter.format(entry.date) // Convert Long to String date
        }

        // Log data setelah dikelompokkan berdasarkan hari
        Log.d("EmotionStatistics", "Grouped by day: ${groupedByDay.map { it.key to it.value.map { entry -> entry.id } }}")

        // Hitung jumlah per emosi dan log hasilnya
        val result = groupedByDay.mapValues { (_, entries) ->
            entries.groupingBy { it.emotion.orEmpty() }.eachCount() // Hitung jumlah per emosi
        }

        // Log hasil akhir (jumlah per emosi per hari)
        Log.d("EmotionStatistics", "Result: ${result.map { it.key to it.value }}")

        return result
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

    suspend fun getQuote(token: String, dominantEmotion: String): QuoteResponse? {
        // Cek apakah API menerima request dengan benar
        Log.d("APIRequest", "Token: $token, Dominant Emotion: $dominantEmotion")

        try {
            val response = RetrofitInstance.apiService.getQuote("Bearer $token", dominantEmotion)  // Gantilah dengan pemanggilan API yang kamu gunakan

            if (response.isSuccessful) {
                Log.d("APIResponse", "Response body: ${response.body()}")  // Log respons API
                return response.body() // Pastikan response.body() berisi data yang benar
            } else {
                Log.e("APIResponse", "Error: ${response.code()} - ${response.message()}")  // Log error jika API mengembalikan kode selain 200
            }
        } catch (e: Exception) {
            Log.e("APIResponse", "Exception: ${e.message}")  // Log jika terjadi error saat pemanggilan API
        }
        return null
    }

    suspend fun deleteAllDiaries(): Boolean {
        return try {
            diaryDao.deleteAll() // Assuming this will delete all diaries from the database
            true // Return true if the deletion is successful
        } catch (e: Exception) {
            Log.e("DiaryRepository", "Failed to delete all diaries: ${e.message}")
            false // Return false if an error occurs
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