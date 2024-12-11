package com.dicoding.capstone_diy.ui.statistic

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.launch

class StatisticViewModel(private val repository: DiaryRepository, private val context: Context) : ViewModel() {

    private val _emotionStatistics = MutableLiveData<Map<String, Int>>()
    val emotionStatistics: LiveData<Map<String, Int>> = _emotionStatistics

    private val _dailyDominantEmotionStatistics = MutableLiveData<Map<String, Pair<String, Int>>>()
    val dailyDominantEmotionStatistics: LiveData<Map<String, Pair<String, Int>>> = _dailyDominantEmotionStatistics


    fun loadDailyDominantEmotionStatistics() {
        viewModelScope.launch {
            val dailyStats = repository.getEmotionStatisticsByDay()

            // Cari emosi dominan per hari
            val dominantStats = dailyStats.mapValues { (_, emotions) ->
                emotions.maxByOrNull { it.value }?.toPair() ?: ("Neutral" to 0)
            }

            // Simpan hasil ke LiveData
            _dailyDominantEmotionStatistics.value = dominantStats

            // Log hasil emosi dominan per hari
            dominantStats.forEach { (date, dominant) ->
                Log.d("DailyDominantEmotion", "Date: $date, Dominant: ${dominant.first} (${dominant.second})")
            }
        }
    }


    private val _dominantEmotion = MutableLiveData<String>()
    val dominantEmotion: LiveData<String> = _dominantEmotion

    fun loadEmotionStatisticsForLastWeek() {
        viewModelScope.launch {
            val statistics = repository.getEmotionStatisticsForLastWeek()
            _emotionStatistics.value = statistics

            // Cari emosi dominan
            val maxCount = statistics.values.maxOrNull() ?: 0
            val dominantEmotions = statistics.filter { it.value == maxCount }.keys

            // Jika ada lebih dari satu emosi dengan jumlah tertinggi yang sama, pilih "Neutral"
            val dominant = if (dominantEmotions.size > 1) {
                "neutral"
            } else {
                dominantEmotions.firstOrNull() ?: "neutral"
            }

            _dominantEmotion.value = dominant
            Log.d("EmotionStats", "Dominant Emotion: $dominant")
            statistics.forEach { (emotion, count) ->
                Log.d("EmotionStats", "$emotion: $count")
            }
        }
    }

    // Add a LiveData for the quote
    private val _quote = MutableLiveData<String>()
    val quote: LiveData<String> = _quote

    // Add a method to fetch the quote
    fun fetchQuote() {
        val token = getToken()
        val dominantEmotion = _dominantEmotion.value

        Log.d("QuoteFetch", "Token: $token")  // Log token yang diambil
        Log.d("QuoteFetch", "Dominant Emotion: $dominantEmotion")

        if (token != null && dominantEmotion != null) {
            viewModelScope.launch {
                try {
                    Log.d("QuoteFetch", "Making API call...")  // Log sebelum API call

                    val response = repository.getQuote(token, dominantEmotion)

                    if (response != null) {
                        Log.d("QuoteFetch", "API response: $response")  // Log jika respons diterima
                        _quote.value = response.quote // Assuming the quote is inside `QuoteResponse` and the field is `quote`
                    } else {
                        Log.d("QuoteFetch", "No response from API")  // Log jika respons kosong
                        _quote.value = "No quote available"
                    }
                } catch (e: Exception) {
                    Log.e("QuoteFetch", "Error fetching quote: ${e.message}")  // Log jika ada exception
                    _quote.value = "No quote available"
                }
            }
        } else {
            Log.d("QuoteFetch", "Token or Dominant Emotion is null")  // Log jika token atau emosi dominan null
            _quote.value = "No quote available"
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("firebase_id_token", null)
        if (token != null) {
            Log.d("StatisticViewModel", "Token berhasil diambil: $token")
        } else {
            Log.e("StatisticViewModel", "Token tidak ditemukan")
        }
        return token
    }

}
