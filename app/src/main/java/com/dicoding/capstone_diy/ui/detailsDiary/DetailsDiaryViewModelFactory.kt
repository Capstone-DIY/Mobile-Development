package com.dicoding.capstone_diy.ui.detailsDiary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.ui.details.DetailsDiaryViewModel

class DetailsDiaryViewModelFactory(
    private val repository: DiaryRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsDiaryViewModel::class.java)) {
            return DetailsDiaryViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
