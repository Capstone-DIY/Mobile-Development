package com.dicoding.capstone_diy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Insert
    suspend fun insertDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)

    @Query("SELECT * FROM diary_table ORDER BY date DESC")
    fun getAllDiaries(): Flow<List<Diary>>
}
