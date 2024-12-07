package com.dicoding.capstone_diy.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("status_code")
	val statusCode: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("firebase_uid")
	val firebaseUid: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
