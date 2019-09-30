package com.larryhsiao.nyx.backup.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.gson.JsonParser
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.media.room.MediaEntity
import com.larryhsiao.nyx.tag.room.TagDiaryEntity
import com.larryhsiao.nyx.tag.room.TagEntity
import com.silverhetch.clotho.file.FileText
import com.silverhetch.clotho.file.TextFile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
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
     * Check the exported file count.
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
                db.mediaDao().create(
                    MediaEntity(
                        0,
                        1,
                        tempMediaFile.toURI().toASCIIString()
                    )
                )
            },
            exportedDir
        ).fire()

        assertEquals(
            5,
            exportedDir.listFiles()[0].listFiles().size
        )
    }

    /**
     * Check the exported json content.
     */
    @Test
    fun checkJsonContents() {
        val tempMediaFile = Files.createTempFile("Temp", "").toFile().also {
            TextFile(it, "smaple ").value()
        }
        val exportedDir = Files.createTempDirectory("temp").toFile()
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
                db.tagDiaryDao().create(TagDiaryEntity(0, 1, 1))
            },
            exportedDir
        ).fire()

        val instance = exportedDir.listFiles()[0]
        assertEquals(
            // language=JSON
            """[{"id":1,"title":"title1","timestamp":0}]""",
            FileText(File(instance, "diary.json")).value()
        )
        assertEquals(
            1,
            JsonParser().parse(
                FileText(File(instance, "media.json")).value()
            ).asJsonArray.size()
        )
        assertEquals(
            // language=JSON
            """[{"id":1,"title":"Tag1"}]""",
            FileText(File(instance, "tag.json")).value()
        )
        assertEquals(
            // language=json
            """[{"id":1,"diaryId":1,"tagId":1}]""",
            FileText(File(instance, "tag_diary.json")).value()
        )
    }
}