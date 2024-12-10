package com.dicoding.capstone_diy.ui.addDiary

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.data.DiaryRequest
import kotlinx.coroutines.launch

class AddDiaryViewModel(private val context: Context) : ViewModel() {

    // Mengambil token dari SharedPreferences
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("firebase_id_token", null)
        if (token != null) {
            Log.d("AddDiaryViewModel", "Token berhasil diambil: $token")
        } else {
            Log.e("AddDiaryViewModel", "Token tidak ditemukan")
        }
        return token
    }

    // Fungsi untuk menambahkan diary
    fun insertDiary(title: String, story: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = getToken()
        if (token == null) {
            onError("Token not found. Please log in again.")
            Log.e("AddDiaryViewModel", "Token is null")
            return
        }

        val diaryRequest = DiaryRequest(title = title, story = story)

        viewModelScope.launch {
            try {
                // Send the API request with the token in the header
                val response = RetrofitInstance.apiService.createDiary("Bearer $token", diaryRequest)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    // Log the response code and error body
                    Log.e("AddDiaryViewModel", "API Error: ${response.code()} ${response.message()}")
                    response.errorBody()?.let { errorBody ->
                        Log.e("AddDiaryViewModel", "Error Body: ${errorBody.string()}")
                    }
                    onError("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Failed to insert diary: ${e.localizedMessage}")
                Log.e("AddDiaryViewModel", "Exception: ${e.localizedMessage}", e)
            }
        }
    }

}

