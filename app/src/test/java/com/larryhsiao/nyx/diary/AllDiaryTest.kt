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
class AllDiaryTest {
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

    @Test
    fun query() {
        NewDiary(
            db.diaryDao(),
            "Title",
            1234567890
        ).value().id()

        assertNotEquals(
            0,
            AllDiary(db.diaryDao()).value().size
        )
    }
}