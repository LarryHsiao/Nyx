package com.larryhsiao.nyx.tag

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.tag.room.TagEntity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [com.larryhsiao.nyx.tag.AttachedTags]
 */
@RunWith(RobolectricTestRunner::class)
class AttachedTagsTest {
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
     * Check the attached tag
     */
    @Test
    fun simple() {
        val title = "This is sample"
        val diaryId = db.diaryDao().create(DiaryEntity(0, "This is title ", 0L))
        db.tagDao().create(TagEntity(0, "This is sample 2"))
        val tagId = db.tagDao().create(TagEntity(0, title))
        NewTagAttachment(db, tagId, diaryId).fire()

        val tags = AttachedTags(db, diaryId).value()

        assertEquals(title, tags[0].title())
        assertEquals(1, tags.size)
    }
}