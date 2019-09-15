package com.larryhsiao.nyx.web

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.larryhsiao.nyx.database.RDatabase
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Local Web access test for development purpose.
 * This test is ignored in building process which have no asset statement and
 * take many minutes to finish.
 */
@Ignore
@RunWith(RobolectricTestRunner::class)
class HostWebAccessTest {
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
     * Test for running web access on development pc.
     */
    @Test
    fun development() {
        TakesAccess(
            ApplicationProvider.getApplicationContext(),
            db,
            8080
        ).run {
            enable()
            Thread.sleep(1000000)
            disable()
        }
    }
}