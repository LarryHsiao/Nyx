package com.larryhsiao.nyx.diary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test for [DiaryByDateTest]
 */
@RunWith(RobolectricTestRunner::class)
class DiaryByDateTest {
    companion object{
        private const val ARG_TITLE = "title"
    }
    private lateinit var db: RDatabase
    /**
     * construct the database in memory for testing.
     */
    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    /**
     * Release database
     */
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
            ApplicationProvider.getApplicationContext(),
            db,
            ARG_TITLE,
            1234567890,
            listOf()
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
            ApplicationProvider.getApplicationContext(),
            db,
            ARG_TITLE,
            1561190477,
            listOf()
        ).value().id()

        NewDiary(
            ApplicationProvider.getApplicationContext(),
            db,
            ARG_TITLE,
            1551190477,
            listOf()
        ).value().id()

        NewDiary(
            ApplicationProvider.getApplicationContext(),
            db,
            ARG_TITLE,
            1551190477,
            listOf()
        ).value().id()

        assertEquals(
            1,
            DiaryByDate(db.diaryDao(), 1561161600).value().size
        )
    }
}