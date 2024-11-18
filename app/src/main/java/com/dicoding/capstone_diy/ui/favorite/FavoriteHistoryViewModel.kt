package com.dicoding.capstone_diy.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteHistoryViewModel : ViewModel() {

    // LiveData untuk daftar favorit
    private val _favoriteList = MutableLiveData<List<FavoriteItem>>()
    val favoriteList: LiveData<List<FavoriteItem>> get() = _favoriteList

    init {
        loadFavorites()
    }

    // Memuat data dummy favorit
    private fun loadFavorites() {
        _favoriteList.value = listOf(
            FavoriteItem(
                title = "Progress Project",
                description = "More Bisa!",
                date = "Thursday, 14 November 2024"
            ),
            FavoriteItem(
                title = "Complete Assignment",
                description = "Finish Math Homework",
                date = "Wednesday, 13 November 2024"
            )
        )
    }
}
