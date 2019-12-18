package com.larryhsiao.nyx.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.larryhsiao.nyx.diary.room.DiaryDao
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.tag.room.TagEntity
import com.larryhsiao.nyx.media.room.MediaDao
import com.larryhsiao.nyx.media.room.MediaEntity
import com.larryhsiao.nyx.tag.room.TagDao
import com.larryhsiao.nyx.tag.room.TagDiaryDao
import com.larryhsiao.nyx.tag.room.TagDiaryEntity
import com.larryhsiao.nyx.weather.room.WeatherDao
import com.larryhsiao.nyx.weather.room.WeatherEntity
import com.silverhetch.clotho.Source

/**
 * Database implemented with Room
 */
@Database(
    entities = arrayOf(
        DiaryEntity::class,
        MediaEntity::class,
        TagEntity::class,
        TagDiaryEntity::class,
        WeatherEntity::class
    ),
    version = 4
)
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
     * Obtain the tag dao.
     */
    abstract fun tagDao(): TagDao

    /**
     * Obtain the tag-diary relation dao
     */
    abstract fun tagDiaryDao(): TagDiaryDao

    /**
     * Obtain the weather dao.
     */
    abstract fun weatherDao(): WeatherDao

    /**
     * Source generate [RDatabase] for Nyx.
     */
    class Singleton(private val context: Context) : Source<RDatabase> {
        companion object {
            private const val DATABASE_NAME = "database_nyx"
            private lateinit var database: RDatabase

            private fun db(context: Context): RDatabase {
                return if (::database.isInitialized) {
                    database
                } else {
                    Room.databaseBuilder(
                        context,
                        RDatabase::class.java,
                        DATABASE_NAME
                    ).addMigrations(
                        Migration1To2(),
                        Migration2To3(),
                        Migration3To4()
                    ).build()
                        .also { database = it }
                }
            }
        }

        override fun value(): RDatabase {
            return db(context)
        }
    }
}