package com.larryhsiao.nyx.diary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.larryhsiao.nyx.RDatabase
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NewDiaryTest {
    private lateinit var db: RDatabase

    @Before
    fun initDb(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RDatabase::class.java
        ).allowMainThreadQueries().build()
    }

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