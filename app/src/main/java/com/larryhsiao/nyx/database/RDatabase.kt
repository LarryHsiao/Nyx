package com.larryhsiao.nyx.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.larryhsiao.nyx.diary.room.*
import com.larryhsiao.nyx.media.room.MediaDao
import com.larryhsiao.nyx.media.room.MediaEntity
import com.silverhetch.clotho.Source

/**
 * Database implemented with Room
 */
@Database(entities = [DiaryEntity::class, MediaEntity::class, TagEntity::class], version = 2)
abstract class RDatabase : RoomDatabase() {

    /**
     * Obtain the Diary dao.
     */
    abstract fun diaryDao(): DiaryDao

    /**
     * Obtain the Media dao.
     */
    abstract fun mediaDao(): MediaDao

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
                RDatabase::class.java,
                DATABASE_NAME
            ).addMigrations(Migration1To2()).build()
        }
    }
}