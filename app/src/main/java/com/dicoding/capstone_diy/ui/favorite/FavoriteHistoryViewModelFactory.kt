package com.dicoding.capstone_diy.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.capstone_diy.data.DiaryRepository

class FavoriteHistoryViewModelFactory(
    private val repository: DiaryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteHistoryViewModel::class.java)) {
            return FavoriteHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
