package com.dicoding.capstone_diy.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteHistoryViewModel : ViewModel() {

    private val _favoriteList = MutableLiveData<List<FavoriteItem>>()
    val favoriteList: LiveData<List<FavoriteItem>> get() = _favoriteList

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        _favoriteList.value = listOf(
            FavoriteItem(
                title = "Progress Project",
                description = "Hore bisa!!",
                date = "Thursday, 14 November 2024",
                emotion = "Happy"
            ),
            FavoriteItem(
                title = "Complete Assignment",
                description = "Finish Math Homework",
                date = "Wednesday, 13 November 2024",
                emotion = "Sadness"
            ),
            FavoriteItem(
                title = "Final Presentation",
                description = "Prepare slides for final presentation.",
                date = "Tuesday, 12 November 2024",
                emotion = "Angry"
            )
        )
    }
}