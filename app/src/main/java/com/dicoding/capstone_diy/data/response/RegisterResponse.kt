package com.dicoding.capstone_diy.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("status_code")
	val statusCode: Int? = null,

	@field:SerializedName("data")
	val data: RegisterData? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class RegisterData(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("firebase_uid")
	val firebaseUid: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
