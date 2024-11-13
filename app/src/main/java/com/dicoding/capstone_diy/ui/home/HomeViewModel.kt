package com.dicoding.capstone_diy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Silahkan Tambahkan Cerita Keseharian Anda "
    }
    val text: LiveData<String> = _text
}