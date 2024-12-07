package com.dicoding.capstone_diy.data.response

data class ProfileResponse(
    val status_code: Int,
    val message: String,
    val data: UserData
)

data class UserData(
    val name: String?,
    val username: String?,
    val dob: String?,
    val contact_number: String?,
    val gender: String?
)
