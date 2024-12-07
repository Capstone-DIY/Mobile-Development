package com.dicoding.capstone_diy.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    private val _registerResult = MutableLiveData<ResultState>()
    val registerResult: LiveData<ResultState> get() = _registerResult

    // Fungsi untuk melakukan registrasi
    fun registerUser(name: String, email: String, password: String, contactNumber: String) {
        Log.d("SignUpViewModel", "Registering user with name: $name, email: $email, contactNumber: $contactNumber")

        _registerResult.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val user = mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password,
                    "contact_number" to contactNumber // Tambahkan contact_number
                )

                // Menggunakan suspend function untuk request
                val response: Response<RegisterResponse> = RetrofitInstance.userService.registerUser(user)

                if (response.isSuccessful) {
                    Log.d("SignUpViewModel", "Registration successful: ${response.body()?.message}")
                    _registerResult.value = ResultState.Success(response.body())
                } else {
                    Log.e("SignUpViewModel", "Registration failed: ${response.message()}")
                    _registerResult.value = ResultState.Error("Registration failed")
                }
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Error: ${e.message}")
                _registerResult.value = ResultState.Error("Error: ${e.message}")
            }
        }
    }

    // State untuk menampung hasil dari API request
    sealed class ResultState {
        data class Success(val data: RegisterResponse?) : ResultState()
        data class Error(val message: String) : ResultState()
        object Loading : ResultState()
    }
}
