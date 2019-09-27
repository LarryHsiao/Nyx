package com.larryhsiao.nyx.diary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.source.ConstSource
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [com.larryhsiao.nyx.diary.DiaryByFilteredDate]
 */
@RunWith(RobolectricTestRunner::class)
class DiaryByFilteredDateTest {
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
     * check input/output
     */
    @Test
    fun simple() {
        Assert.assertEquals(
            2,
            DiaryByFilteredDate(
                ConstSource(
                    listOf(
                        PhantomDiary(0),
                        PhantomDiary(10000),
                        PhantomDiary(999999999)
                    )
                ),
                0
            ).value().size
        )
    }
}