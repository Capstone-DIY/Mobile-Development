package com.dicoding.capstone_diy.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {

    val allDiaries: Flow<List<Diary>> = diaryDao.getAllDiaries()

    suspend fun insert(diary: Diary) {
        try {
            diaryDao.insertDiary(diary)
            Log.d("DiaryRepository", "Diary inserted successfully: $diary")
        } catch (e: Exception) {
            Log.e("DiaryRepository", "Error inserting diary: ${e.message}")
        }
    }

    suspend fun delete(diary: Diary) {
        diaryDao.deleteDiary(diary)
    }
}
