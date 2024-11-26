package com.dicoding.capstone_diy.ui.addDiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.launch


class AddDiaryViewModel (private val repository: DiaryRepository): ViewModel() {
    // Data diary internal sebagai MutableLiveData
    private val _diaries = MutableLiveData<List<Diary>>(mutableListOf())
    val diaries: LiveData<List<Diary>> get() = _diaries

    // Menambahkan diary baru
    fun insertDiary(diary: Diary) {
        viewModelScope.launch {
            try {
                repository.insert(diary)
                Log.d("AddDiaryViewModel", "Diary inserted: $diary")
            } catch (e: Exception) {
                Log.e("AddDiaryViewModel", "Error inserting diary: ${e.message}")
            }
        }
    }

}
