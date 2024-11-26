package com.dicoding.capstone_diy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _diaries = MutableLiveData<List<Diary>>()
    val diaries: LiveData<List<Diary>> get() = _diaries

    init {
        // Mengambil data dari repository menggunakan Flow
        viewModelScope.launch {
            repository.allDiaries.collect { diaryList ->
                _diaries.value = diaryList
            }
        }
    }
}
