package com.dicoding.capstone_diy.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone_diy.api.RetrofitInstance
import com.dicoding.capstone_diy.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun loginWithBackendAPI(email: String, password: String) {
        _loginResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.loginUser(
                    mapOf("email" to email, "password" to password)
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        _loginResult.value = Result.Success(it)
                    } ?: run {
                        _loginResult.value = Result.Error("Login response is null")
                    }
                } else {
                    _loginResult.value = Result.Error("API error: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginResult.value = Result.Error("Network error: ${e.message}")
            }
        }
    }

    fun saveToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putString("firebase_id_token", token)
            .apply()
    }

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPref.getString("firebase_id_token", null)
    }

    sealed class Result<out T> {
        object Loading : Result<Nothing>()
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }
}
