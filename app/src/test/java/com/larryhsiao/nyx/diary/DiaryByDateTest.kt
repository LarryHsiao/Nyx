package com.larryhsiao.nyx.diary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.RDatabase
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DiaryByDateTest {
    private lateinit var db: RDatabase

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun releaseDb() {
        db.close()
    }

    /**
     * Query all if timestamp is zero
     */
    @Test
    fun allDiary() {
        NewDiary(
            db.diaryDao(),
            "Title",
            1234567890
        ).value().id()

        assertEquals(
            1,
            DiaryByDate(db.diaryDao(), 0).value().size
        )
    }


    /**
     * Query by date
     */
    @Test
    fun diaryByDateTimestamp() {
        NewDiary(
            db.diaryDao(),
            "Title",
            1561190477
        ).value().id()

        NewDiary(
            db.diaryDao(),
            "Title",
            1551190477
        ).value().id()

        NewDiary(
            db.diaryDao(),
            "Title",
            1551190477
        ).value().id()

        assertEquals(
            1,
            DiaryByDate(db.diaryDao(), 1561161600).value().size
        )
    }
}