package com.dicoding.capstone_diy.ui.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.launch

class StatisticViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _emotionStatistics = MutableLiveData<Map<String, Int>>()
    val emotionStatistics: LiveData<Map<String, Int>> = _emotionStatistics

    private val _dominantEmotion = MutableLiveData<String>()
    val dominantEmotion: LiveData<String> = _dominantEmotion

    fun loadEmotionStatisticsForLastWeek() {
        viewModelScope.launch {
            val statistics = repository.getEmotionStatisticsForLastWeek()
            _emotionStatistics.value = statistics

            // Cari emosi dominan
            val dominant = statistics.maxByOrNull { it.value }?.key ?: "Neutral"
            _dominantEmotion.value = dominant
        }
    }
}
