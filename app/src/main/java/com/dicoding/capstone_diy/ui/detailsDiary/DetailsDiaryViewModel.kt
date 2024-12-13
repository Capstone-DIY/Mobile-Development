package com.dicoding.capstone_diy.ui.details

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryRepository
import kotlinx.coroutines.launch

class DetailsDiaryViewModel(private val repository: DiaryRepository, private val context: Context) : ViewModel() {

    private val _diary = MutableLiveData<Diary>()
    val diary: LiveData<Diary> get() = _diary

    fun updateDiary(diary: Diary) {
        viewModelScope.launch {
            repository.update(diary)
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("firebase_id_token", null)
        if (token != null) {
            Log.d("DetailsDiaryViewModel", "Token berhasil diambil: $token")
        } else {
            Log.e("DetailsDiaryViewModel", "Token tidak ditemukan")
        }
        return token
    }

    fun deleteDiary(diary: Diary) {
        viewModelScope.launch {
            val token = getToken()

            Log.d("HomeViewModel", "Using token: $token")
            if (token != null) {
                val result = repository.deleteDiaryFromApi(token, diary.id)
                result.onSuccess {
                    Log.d("DetailsDiaryViewModel", "Diary deleted successfully")
                }.onFailure { exception ->
                    Log.e("DetailsDiaryViewModel", "Error deleting diary: ${exception.message}")
                }
            }
        }
    }


    fun setDiaryData(diary: Diary) {
        _diary.value = diary
    }
}
