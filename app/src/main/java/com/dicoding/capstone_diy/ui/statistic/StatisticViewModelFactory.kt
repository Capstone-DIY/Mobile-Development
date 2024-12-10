package com.dicoding.capstone_diy.ui.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.capstone_diy.data.DiaryRepository

class StatisticViewModelFactory(private val repository: DiaryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticViewModel::class.java)) {
            return StatisticViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
