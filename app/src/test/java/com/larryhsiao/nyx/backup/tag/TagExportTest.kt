package com.larryhsiao.nyx.backup.tag

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.room.TagEntity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.StringBuilder

/**
 * Test for [com.larryhsiao.nyx.backup.TagExport]
 */
@RunWith(RobolectricTestRunner::class)
class TagExportTest {
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
     * Check the output
     */
    @Test
    fun simple() {
        db.tagDao().create(TagEntity(0, "Tag1"))
        db.tagDao().create(TagEntity(0, "Tag2"))

        val result = StringBuilder()
        TagExport(
            db.tagDao()
        ).value().forEach {
            result.append(it)
        }

        assertEquals(
            """{"id":1,"title":"Tag1"}{"id":2,"title":"Tag2"}""",
            result.toString()
        )
    }
}