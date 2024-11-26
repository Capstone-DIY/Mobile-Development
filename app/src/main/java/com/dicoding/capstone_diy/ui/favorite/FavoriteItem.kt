package com.dicoding.capstone_diy.ui.favorite

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class FavoriteItem(
    val title: String,
    val description: String,
    val date: String,
    val emotion: String
) : Parcelable
