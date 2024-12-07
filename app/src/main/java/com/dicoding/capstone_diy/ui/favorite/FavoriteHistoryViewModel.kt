package com.dicoding.capstone_diy.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteHistoryViewModel(private val repository: DiaryRepository) : ViewModel() {

    val favorites = repository.getFavoriteDiaries() // LiveData langsung dari repository

    fun updateDiary(diary: Diary) {
        viewModelScope.launch {
            repository.update(diary) // Untuk memperbarui entri favorit/non-favorit
        }
    }
}