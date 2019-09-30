package com.larryhsiao.nyx.backup.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.config.ConfigImpl
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.room.MediaEntity
import com.larryhsiao.nyx.tag.room.TagDiaryEntity
import com.larryhsiao.nyx.tag.room.TagEntity
import com.silverhetch.clotho.file.FileDelete
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
 * Test for [com.larryhsiao.nyx.backup.local.Restore]
 */
@RunWith(RobolectricTestRunner::class)
class RestoreTest {
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
     * restore to empty
     */
    @Test
    fun restoreToEmpty() {
        val sampleText = "sample "
        val config = ConfigImpl(ApplicationProvider.getApplicationContext())
        val tempMediaFile = Files.createTempFile(
            config.mediaRoot().toPath(),
            "Temp",
            ""
        ).toFile().also { TextFile(it, sampleText).value() }
        NewBackup(
            db.also { db ->
                db.diaryDao().create(DiaryEntity(0, "title1", 0L))
                db.mediaDao().create(
                    MediaEntity(
                        0,
                        1,
                        tempMediaFile.toURI().toASCIIString()
                    )
                )
                db.tagDao().create(TagEntity(0, "Tag1"))
                db.tagDao().create(TagEntity(0, "Tag2"))
                db.tagDiaryDao().create(TagDiaryEntity(0, 1, 2))
            },
            config.backupRoot()
        ).fire()

        db.clearAllTables()
        FileDelete(config.mediaRoot()).fire()

        Restore(
            config.backupRoot().listFiles()[0],
            config.mediaRoot(),
            db
        ).fire()

        assertEquals(1, db.mediaDao().all().size)
        assertEquals(1, config.mediaRoot().listFiles().size)
        assertEquals(1, db.diaryDao().all().size)
        assertEquals(2, db.tagDao().all().size)
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