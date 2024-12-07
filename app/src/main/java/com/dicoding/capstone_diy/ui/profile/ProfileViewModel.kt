package com.dicoding.capstone_diy.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.response.UserData
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<UserData?>()
    val userProfile: LiveData<UserData?> = _userProfile

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchUserProfile(token: String) {
        viewModelScope.launch {
            try {
                // Pass the token in the header of the API request
                val response = RetrofitInstance.userService.getUserProfile("Bearer $token")

                if (response.isSuccessful) {
                    val userData = response.body()?.data
                    if (userData != null) {
                        val formattedDob = userData.dob.split("T").firstOrNull() ?: userData.dob
                        _userProfile.postValue(userData.copy(dob = formattedDob))
                        Log.d("ProfileViewModel", "User Data: $userData")
                    } else {
                        _errorMessage.postValue("User profile data is null")
                        Log.e("ProfileViewModel", "Response data is null: ${response.body()}")
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.code()} ${response.message()}")
                    Log.e(
                        "ProfileViewModel",
                        "Error Response: ${response.code()} ${response.message()} ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to fetch user data: ${e.localizedMessage}")
                Log.e("ProfileViewModel", "Exception: ${e.localizedMessage}", e)
            }
        }
    }


    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPreferences.getString("firebase_id_token", null) // Make sure to store token in SharedPreferences when user logs in
    }


}