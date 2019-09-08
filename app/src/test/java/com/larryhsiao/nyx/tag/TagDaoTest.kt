package com.larryhsiao.nyx.tag

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.room.TagEntity
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test for TagDao
 */
@RunWith(RobolectricTestRunner::class)
class TagDaoTest {
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
     * Check is null if find a non exist entity.
     */
    @Test
    fun nullIfNotExist() {
        Assert.assertNull(
            db.tagDao().byName("This is sample title")
        )
    }

    /**
     * Check finding entity by title
     */
    @Test
    fun findByName() {
        val title = "This is sample"
        db.tagDao().create(TagEntity(0, title))
        Assert.assertEquals(
            title,
            db.tagDao().byName(title)?.title
        )
    }

    /**
     * Check the returning entity have valid id with conflict
     */
    @Test
    fun conflictCreateId() {
        val title = "this is sample title"
        db.tagDao().queryOrCreate(title)
        Assert.assertEquals(
            1,
            db.tagDao().queryOrCreate(title).id
        )
    }
}
