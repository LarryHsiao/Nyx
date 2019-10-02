package com.larryhsiao.nyx.backup.tag

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.tag.room.TagDiaryEntity
import com.larryhsiao.nyx.tag.room.TagEntity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.StringBuilder

/**
 * Test for [com.larryhsiao.nyx.backup.tag.TagDiaryExport]
 */
@RunWith(RobolectricTestRunner::class)
class TagDiaryExportTest {
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
        db.diaryDao().create(DiaryEntity(0, "diary", 0L))
        db.tagDiaryDao().create(TagDiaryEntity(0, 1, 2))

        val result = StringBuilder()
        TagDiaryExport(
            db.tagDiaryDao()
        ).value().forEach {
            result.append(it)
        }

        assertEquals(
            """{"id":1,"diaryId":1,"tagId":2}""",
            result.toString()
        )
    }
}