package com.dicoding.capstone_diy.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteHistoryViewModel(private val repository: DiaryRepository) : ViewModel() {

    // Menggunakan Flow untuk mendapatkan daftar favorit
    val favorites: Flow<List<Diary>> = repository.getFavoriteDiaries()

    fun updateDiary(diary: Diary) {
        viewModelScope.launch {
            repository.update(diary) // Memperbarui entri favorit/non-favorit
        }
    }
}
