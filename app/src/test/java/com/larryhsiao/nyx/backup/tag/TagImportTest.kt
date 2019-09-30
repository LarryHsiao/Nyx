package com.larryhsiao.nyx.backup.tag

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.file.TextFile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.FileInputStream

/**
 * Test for [com.larryhsiao.nyx.backup.tag.TagImport]
 */
@RunWith(RobolectricTestRunner::class)
class TagImportTest {
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
        val jsonFile = TextFile(
            File.createTempFile("prefix", ""),
            """[{"id":1,"title":"Tag1"},{"id":2,"title":"Tag2"}]"""
        ).value()

        TagImport(
            db,
            FileInputStream(jsonFile)
        ).fire()

        val tagList = db.tagDao().all()
        assertEquals(2, tagList.size)
        assertEquals("Tag1", db.tagDao().byId(1).title)
        assertEquals("Tag2", db.tagDao().byId(2).title)
    }
}