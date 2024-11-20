package com.dicoding.capstone_diy.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.capstone_diy.data.Diary

class DetailsDiaryViewModel : ViewModel() {

    private val _diary = MutableLiveData<Diary>()
    val diary: LiveData<Diary> get() = _diary

    fun setDiaryData(diary: Diary) {
        _diary.value = diary
    }
}
