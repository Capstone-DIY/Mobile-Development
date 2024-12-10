package com.dicoding.capstone_diy.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)

    @Query("SELECT * FROM diary_table WHERE id = :id")
    suspend fun getDiaryById(id: Int): Diary?

    @Query("SELECT * FROM diary_table ORDER BY date DESC")
    fun getAllDiaries(): Flow<List<Diary>>

    @Query("SELECT * FROM diary_table WHERE favorited = 1 ORDER BY date DESC")
    fun getFavorites(): Flow<List<Diary>>

    @Update
    suspend fun updateDiary(diary: Diary)

    @Query("SELECT * FROM diary_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getDiaryBetweenDates(startDate: Long, endDate: Long): List<Diary>

}
