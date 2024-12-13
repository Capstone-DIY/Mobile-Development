package com.dicoding.capstone_diy.data.response

data class DiaryApiResponse(
    val status_code: Int,
    val message: String,
    val data: List<DiaryData> // Ubah dari DiaryData menjadi List<DiaryData>
)

data class SingleDiaryResponse(
    val status_code: Int,
    val message: String,
    val data: DiaryData
)


data class DiaryData(
    val id: Int,
    val date: String,
    val title: String,
    val story: String,
    val emotion: String? = null,
    val response: String? = null
)
