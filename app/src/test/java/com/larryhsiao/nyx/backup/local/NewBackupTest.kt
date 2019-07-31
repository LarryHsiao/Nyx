package com.larryhsiao.nyx.backup.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.room.MediaEntity
import com.silverhetch.clotho.file.TextFile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.nio.file.Files

/**
 * Test for [com.larryhsiao.nyx.backup.local.NewBackup]
 */
@RunWith(RobolectricTestRunner::class)
class NewBackupTest {
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
     * Check the exported file names.
     */
    @Test
    fun checkFileName() {
        val tempMediaFile = Files.createTempFile("Temp", "").toFile().also {
            TextFile(it, "smaple ").value()
        }
        val exportedDir = Files.createTempDirectory("temp").toFile()
        NewBackup(
            db.also { db ->
                db.diaryDao().create(DiaryEntity(0, "title1", 0L))
                db.mediaDao()
                    .create(MediaEntity(0, 1, tempMediaFile.toURI().toASCIIString()))
            },
            exportedDir
        ).fire()

        assertEquals(
            3,
            exportedDir.listFiles()[0].listFiles().size
        )
    }
}