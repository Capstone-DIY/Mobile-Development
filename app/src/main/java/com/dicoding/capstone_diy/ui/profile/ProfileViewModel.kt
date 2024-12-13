package com.dicoding.capstone_diy.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.data.response.UserData
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<UserData?>()
    val userProfile: LiveData<UserData?> = _userProfile

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    fun fetchUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.getUserProfile("Bearer $token")

                if (response.isSuccessful) {
                    val userData = response.body()?.data
                    if (userData != null) {
                        val formattedDob = userData.dob?.split("T")?.firstOrNull() ?: "-"
                        _userProfile.postValue(userData.copy(dob = formattedDob))
                    } else {
                        _errorMessage.postValue("User profile data is null")
                    }
                } else {
                    if (response.code() == 403) {
                        _errorMessage.postValue("Token expired or invalid. Please login again.")
                    } else {
                        _errorMessage.postValue("Error: ${response.code()} ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to fetch user data: ${e.localizedMessage}")
            }
        }
    }


    fun deleteAllDiaries() {
        viewModelScope.launch {
            try {
                val result = repository.deleteAllDiaries()
                if (result) {
                    _successMessage.postValue("All diaries have been successfully deleted.")
                } else {
                    _errorMessage.postValue("Failed to delete all diaries.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to delete all diaries: ${e.message}")
            }
        }
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPreferences.getString("firebase_id_token", null)
    }


}