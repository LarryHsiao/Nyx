package com.larryhsiao.nyx

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.larryhsiao.nyx.diary.DiaryDao
import com.larryhsiao.nyx.diary.RDiary
import com.silverhetch.clotho.Source

@Database(entities = [RDiary::class], version = 1)
abstract class RDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao

    /**
     * Source generate [RDatabase] for Nyx.
     */
    class Factory(private val context: Context) : Source<RDatabase> {
        companion object {
            private const val DATABASE_NAME = "database_nyx"
        }

        override fun value(): RDatabase {
            return Room.databaseBuilder(
                context,
                RDatabase::class.java, DATABASE_NAME
            ).build()
        }
    }
}