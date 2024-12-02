package com.dicoding.capstone_diy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Diary::class], version = 2, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile
        private var INSTANCE: DiaryDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Tambahkan kolom baru ke tabel Diary
                database.execSQL("ALTER TABLE diary_table ADD COLUMN favorited INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): DiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiaryDatabase::class.java,
                    "diary_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
