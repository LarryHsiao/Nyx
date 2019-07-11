package com.larryhsiao.nyx.diary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test for [NewDiaryTest]
 */
@RunWith(RobolectricTestRunner::class)
class NewDiaryTest {
    private lateinit var db: RDatabase

    /**
     * construct the database in memory for testing.
     */
    @Before
    fun initDb(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    /**
     * Release database
     */
    @After
    fun releaseDb(){
        db.close()
    }

    /**
     * Create an entry.
     */
    @Test
    fun create() {
        assertNotEquals(
            0,
            NewDiary(
                db.diaryDao(),
                "Title",
                1234567890
            ).value().id()
        )
    }
}