package com.larryhsiao.nyx.backup

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.silverhetch.clotho.file.FileText
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.nio.file.Files

/**
 * Test for com.larryhsiao.nyx.backup.DiaryExport
 */
@RunWith(RobolectricTestRunner::class)
class DiaryExportTest {
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
     * Export exist diary entity to file in json.
     */
    @Test
    fun checkResult() {
        val target = Files.createTempFile("prefix", "")

        db.diaryDao().create(
            DiaryEntity(
                0,
                "Test1",
                1000L
            )
        )
        db.diaryDao().create(
            DiaryEntity(
                0,
                "Test2",
                2000L
            )
        )

        DiaryExport(
            db,
            target.toFile()
        ).fire()

        assertEquals(
            """[{"id":2,"title":"Test2","timestamp":2000},{"id":1,"title":"Test1","timestamp":1000}]""",
            FileText(target.toFile()).value()
        )
    }
}