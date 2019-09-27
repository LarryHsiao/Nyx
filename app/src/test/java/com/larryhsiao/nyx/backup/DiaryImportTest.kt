package com.larryhsiao.nyx.backup

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
 * Test for com.larryhsiao.nyx.backup.DiaryImport
 */
@RunWith(RobolectricTestRunner::class)
class DiaryImportTest {
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
     * Import diary with json file
     */
    @Test
    fun simple() {
        val jsonFile = TextFile(
            File.createTempFile("prefix", ""),
            """[{"id":2,"title":"Test2","timestamp":2000},{"id":1,"title":"Test1","timestamp":1000}]"""
        ).value()

        DiaryImport(
            db,
            FileInputStream(jsonFile)
        ).fire()

        val diaryList = db.diaryDao().all()
        assertEquals(2, diaryList.size)
        assertEquals("Test1", db.diaryDao().byId(1).diary.title)
        assertEquals("Test2", db.diaryDao().byId(2).diary.title)
    }
}