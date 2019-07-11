package com.larryhsiao.nyx.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test for com.larryhsiao.nyx.database.RDatabase
 */
@RunWith(AndroidJUnit4::class)
class RDatabaseTest {
    private val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RDatabase::class.java.name
    )

    @Test
    fun migration() {
        helper.also {
            it.createDatabase("test", 1)
            it.runMigrationsAndValidate(
                "test",
                2,
                true,
                Migration1To2()
            )
            assertTrue(true)
        }
    }
}