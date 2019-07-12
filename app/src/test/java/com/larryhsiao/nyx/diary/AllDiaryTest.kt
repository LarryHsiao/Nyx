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
 * Test for [AllDiary]
 */
@RunWith(RobolectricTestRunner::class)
class AllDiaryTest {
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

    @Test
    fun query() {
        NewDiary(
            ApplicationProvider.getApplicationContext(),
            db,
            "Title",
            1234567890,
            listOf()
        ).value().id()

        assertNotEquals(
            0,
            AllDiary(db.diaryDao()).value().size
        )
    }
}