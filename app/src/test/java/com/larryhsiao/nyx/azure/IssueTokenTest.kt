package com.larryhsiao.nyx.azure

import com.larryhsiao.nyx.BuildConfig
import com.larryhsiao.nyx.BuildConfig.AZURE_SECRET_KEY
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test for [com.larryhsiao.nyx.azure.IssueToken]
 */
@RunWith(RobolectricTestRunner::class)
class IssueTokenTest {
    /**
     * Check the endpoint exist
     */
    @Test
    fun simple() {
        assertTrue(IssueToken(AZURE_SECRET_KEY).value().isNotEmpty())
    }
}