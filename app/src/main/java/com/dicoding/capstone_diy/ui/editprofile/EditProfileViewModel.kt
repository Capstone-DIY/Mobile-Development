package com.dicoding.capstone_diy.ui.editprofile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.response.UserData
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private val _profileUpdateResult = MutableLiveData<Boolean>()
    val profileUpdateResult: LiveData<Boolean> = _profileUpdateResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun updateUserProfile(context: Context, name: String, username: String, dob: String, contact: String, gender: String) {
        viewModelScope.launch {
            try {
                val token = getToken(context)
                if (token == null) {
                    _errorMessage.postValue("Authentication token not found")
                    return@launch
                }

                // Format ulang tanggal agar hanya yyyy-MM-dd
                val formattedDob = dob.split("T").firstOrNull() ?: dob

                val profileData = mapOf(
                    "name" to name,
                    "username" to username,
                    "dob" to formattedDob,
                    "contact_number" to contact,
                    "gender" to gender
                )

                val response = RetrofitInstance.userService.updateUserProfile("Bearer $token", profileData)

                if (response.isSuccessful) {
                    _profileUpdateResult.postValue(true)
                } else {
                    _errorMessage.postValue("Update failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    private fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPreferences.getString("firebase_id_token", null)
    }
}
