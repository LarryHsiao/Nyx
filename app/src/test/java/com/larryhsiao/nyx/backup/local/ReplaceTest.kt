package com.larryhsiao.nyx.backup.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.config.ConfigImpl
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.room.MediaEntity
import com.silverhetch.clotho.file.FileText
import com.silverhetch.clotho.file.TextFile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.nio.file.Files

/**
 * Test for [com.larryhsiao.nyx.backup.local.Replace]
 */
@RunWith(RobolectricTestRunner::class)
class ReplaceTest {
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
     * Check input/output
     */
    @Test
    fun restoreWithBackupOutput() {
        val sampleText = "sample "
        val config =
            ConfigImpl(ApplicationProvider.getApplicationContext())

        NewBackup(
            db.also { db ->
                db.diaryDao().create(DiaryEntity(0, "title1", 0L))
                db.mediaDao().create(
                    MediaEntity(
                        0,
                        1,
                        Files.createTempFile(
                            config.mediaRoot().toPath(),
                            "Temp",
                            ""
                        ).toFile().also {
                            TextFile(
                                it,
                                sampleText
                            ).value()
                        }.toURI().toASCIIString()
                    )
                )
                db.mediaDao().create(
                    MediaEntity(
                        0,
                        1,
                        Files.createTempFile(
                            config.mediaRoot().toPath(),
                            "Temp",
                            ""
                        ).toFile().also {
                            TextFile(
                                it,
                                sampleText
                            ).value()
                        }.toURI().toASCIIString()
                    )
                )
            },
            config.backupRoot()
        ).fire()

        Replace(
            config.backupRoot().listFiles()[0],
            config.mediaRoot(),
            db
        ).fire()

        assertEquals(2, db.mediaDao().all().size)
        assertEquals(1, db.diaryDao().all().size)
        assertEquals(2, db.diaryDao().all()[0].mediaEntities.size)
        assertEquals(2, config.mediaRoot().listFiles().size)
        config.mediaRoot().listFiles().forEach {
            assertEquals(
                sampleText,
                FileText(
                    it
                ).value()
            )
        }
    }
}