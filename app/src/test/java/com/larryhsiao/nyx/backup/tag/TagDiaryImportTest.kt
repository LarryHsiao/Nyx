package com.larryhsiao.nyx.backup.tag

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.file.TextFile
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.FileInputStream

/**
 * Test for [com.larryhsiao.nyx.backup.tag.TagDiaryImport]
 */
@RunWith(RobolectricTestRunner::class)
class TagDiaryImportTest {
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
            """[{"id":1,"diaryId":1,"tagId":2},{"id":2,"diaryId":1,"tagId":1}]"""
        ).value()

        TagDiaryImport(
            db,
            FileInputStream(jsonFile)
        ).fire()

        val tagDiaryList = db.tagDiaryDao().all()
        Assert.assertEquals(2, tagDiaryList.size)
        Assert.assertEquals(1, db.tagDiaryDao().all()[0].id)
        Assert.assertEquals(2, db.tagDiaryDao().all()[1].id)
    }
}