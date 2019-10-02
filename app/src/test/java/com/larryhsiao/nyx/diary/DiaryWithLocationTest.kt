package com.larryhsiao.nyx.diary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.room.MediaEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test for [com.larryhsiao.nyx.diary.DiaryWithLocation]
 */
@RunWith(RobolectricTestRunner::class)
class DiaryWithLocationTest {
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
     * Check all of outputs have location
     */
    @Test
    fun allHaveLocations() {
        db.diaryDao().create(DiaryEntity(0, "title", 0L))
        db.diaryDao().create(DiaryEntity(0, "title2", 0L))
        db.mediaDao().create(MediaEntity(0, 2, "geo:100,100"))

        assertEquals(
            1,
            DiaryWithLocation(db).value().size
        )

        assertEquals(
            "geo:100,100",
            db.mediaDao().byDiaryId(
                DiaryWithLocation(db).value()[0].id()
            )[0].uri
        )
    }
}