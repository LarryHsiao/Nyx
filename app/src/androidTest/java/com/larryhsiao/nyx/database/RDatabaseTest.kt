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

    /**
     * Migration test from 1 to 2
     */
    @Test
    fun migration1To2() {
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

    /**
     * Migration test from 2 to 3
     */
    @Test
    fun migration2To3() {
        helper.also {
            it.createDatabase("test", 2)
            it.runMigrationsAndValidate(
                "test",
                3,
                true,
                Migration2To3()
            )
            assertTrue(true)
        }
    }

    /**
     * Migration test from 3 to 4
     */
    @Test
    fun migration3To4() {
        helper.also {
            it.createDatabase("test", 3)
            it.runMigrationsAndValidate(
                "test",
                4,
                true,
                Migration3To4()
            )
            assertTrue(true)
        }
    }
}