package com.dicoding.capstone_diy.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.launch

class DetailsDiaryViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _diary = MutableLiveData<Diary>()
    val diary: LiveData<Diary> get() = _diary

    // Fungsi untuk memperbarui data Diary
    fun updateDiary(diary: Diary) {
        viewModelScope.launch {
            repository.update(diary)
        }
    }

    fun deleteDiary(diary: Diary) {
        viewModelScope.launch {
            repository.delete(diary)
        }
    }


    fun setDiaryData(diary: Diary) {
        _diary.value = diary
    }
}
