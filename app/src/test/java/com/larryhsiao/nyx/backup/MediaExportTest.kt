package com.larryhsiao.nyx.backup

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.media.room.MediaEntity
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test for [com.larryhsiao.nyx.backup.MediaExport]
 */
@RunWith(RobolectricTestRunner::class)
class MediaExportTest {
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
     * Check the media exported json
     */
    @Test
    fun json() {
        db.mediaDao().create(MediaEntity(0, 1, "file://abc.caom"))
        db.mediaDao().create(MediaEntity(0, 2, "file://abc2.caom"))

        val result = StringBuilder()
        MediaExport(
            db.mediaDao()
        ).value().forEach {
            result.append(it.json())
        }

        Assert.assertEquals(
            """{"id":1,"diaryId":1,"uri":"file://abc.caom"}{"id":2,"diaryId":2,"uri":"file://abc2.caom"}""",
            result.toString()
        )
    }

    /**
     * Check the media uri
     */
    @Test
    fun uri() {
        db.mediaDao().create(MediaEntity(0, 1, "file://abc.caom"))
        db.mediaDao().create(MediaEntity(0, 1, "abc://abc.caom"))

        Assert.assertTrue(
            MediaExport(
                db.mediaDao()
            ).value().asSequence().toList()[0].isFile()
        )
        Assert.assertFalse(
            MediaExport(
                db.mediaDao()
            ).value().asSequence().toList()[1].isFile()
        )
    }
}