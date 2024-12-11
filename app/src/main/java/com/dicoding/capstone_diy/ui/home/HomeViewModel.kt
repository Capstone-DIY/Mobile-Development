package com.dicoding.capstone_diy.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.data.DiaryRequest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DiaryRepository, private val context: Context) : ViewModel() {

    private val _diaries = MutableLiveData<List<Diary>>()
    val diaries: LiveData<List<Diary>> get() = _diaries

    private val _apiStatus = MutableLiveData<String>()
    val apiStatus: LiveData<String> get() = _apiStatus

    init {
        // Load data from local database
        viewModelScope.launch {
            repository.allDiaries.collect { diaryList ->
                _diaries.value = diaryList
            }
        }
    }

    // Helper method to get the token from SharedPreferences
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("firebase_id_token", null)
        if (token != null) {
            Log.d("HomeViewModel", "Token berhasil diambil: $token")
        } else {
            Log.e("HomeViewModel", "Token tidak ditemukan")
        }
        return token
    }

    private val _isTokenExpired = MutableLiveData<Boolean>()
    val isTokenExpired: LiveData<Boolean> get() = _isTokenExpired
    /**
     * Fetch diaries from the API and update the local database
     */
    fun fetchDiariesFromApi() {
        viewModelScope.launch {
            _apiStatus.value = "Loading"
            val token = getToken()
            if (token == null) {
                Log.e("HomeViewModel", "Token not found")
                _apiStatus.value = "Error: Token not found"
                return@launch
            }
            Log.d("HomeViewModel", "Using token: $token")
            val result = repository.fetchAllDiariesFromApi(token)
            result.onSuccess { diariesFromApi ->
                _apiStatus.value = "Success"
                Log.d("HomeViewModel", "Diaries fetched successfully")
            }.onFailure { exception ->
                if (exception.message == "Token expired") {
                    _isTokenExpired.value = true
                }
                _apiStatus.value = "Error: ${exception.message}"
                Log.e("HomeViewModel", "Error fetching diaries: ${exception.message}")
            }
        }
    }



    /**
     * Update a diary in the local database
     */
    fun updateDiary(diary: Diary) {
        viewModelScope.launch {
            repository.update(diary)
        }
    }
}


