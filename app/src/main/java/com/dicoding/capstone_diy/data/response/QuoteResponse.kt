package com.dicoding.capstone_diy.data.response

import com.google.gson.annotations.SerializedName

data class QuoteResponse(

	@field:SerializedName("quote")
	val quote: String
)
