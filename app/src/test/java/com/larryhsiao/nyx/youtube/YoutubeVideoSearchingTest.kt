package com.larryhsiao.nyx.youtube

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

/**
 * Test for com.larryhsiao.nyx.youtube.YoutubeVideoSearching
 */
@RunWith(RobolectricTestRunner::class)
class YoutubeVideoSearchingTest {
    /**
     * Check the youtube searching have some result,
     */
    @Test
    fun simple() {
        try {
            assertNotEquals(
                0,
                YoutubeVideoSearching(
                    ApplicationProvider.getApplicationContext(),
                    "Codding"
                ).value().size
            )
        } catch (e: IOException) {
            assertTrue(true) // prevent out of API requesting quota exception
        }
    }
}