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

            val dominantStats = dailyStats.mapValues { (_, emotions) ->
                emotions.maxByOrNull { it.value }?.toPair() ?: ("Neutral" to 0)
            }

            _dailyDominantEmotionStatistics.value = dominantStats

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

            val maxCount = statistics.values.maxOrNull() ?: 0
            val dominantEmotions = statistics.filter { it.value == maxCount }.keys

            val dominant = if (dominantEmotions.size > 1) {
                "Neutral"
            } else {
                dominantEmotions.firstOrNull() ?: "Neutral"
            }

            _dominantEmotion.value = dominant
            Log.d("EmotionStats", "Dominant Emotion: $dominant")
            statistics.forEach { (emotion, count) ->
                Log.d("EmotionStats", "$emotion: $count")
            }
        }
    }

    private val _quote = MutableLiveData<String>()
    val quote: LiveData<String> = _quote

    fun fetchQuote() {
        val token = getToken()
        val dominantEmotion = _dominantEmotion.value

        Log.d("QuoteFetch", "Token: $token")
        Log.d("QuoteFetch", "Dominant Emotion: $dominantEmotion")

        if (token != null && dominantEmotion != null) {
            viewModelScope.launch {
                try {
                    Log.d("QuoteFetch", "Making API call...")

                    val response = repository.getQuote(token, dominantEmotion)

                    if (response != null) {
                        Log.d("QuoteFetch", "API response: $response")
                        _quote.value = response.quote
                    } else {
                        Log.d("QuoteFetch", "No response from API")
                        _quote.value = "No quote available"
                    }
                } catch (e: Exception) {
                    Log.e("QuoteFetch", "Error fetching quote: ${e.message}")
                    _quote.value = "No quote available"
                }
            }
        } else {
            Log.d("QuoteFetch", "Token or Dominant Emotion is null")
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
