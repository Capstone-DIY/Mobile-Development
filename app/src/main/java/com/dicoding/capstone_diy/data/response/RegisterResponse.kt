package com.dicoding.capstone_diy.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("status_code")
	val statusCode: Int,

	@field:SerializedName("data")
	val data: UserData,

	@field:SerializedName("message")
	val message: String
)

data class UserData(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String
)
