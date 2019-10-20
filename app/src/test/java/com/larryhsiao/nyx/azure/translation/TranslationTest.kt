package com.larryhsiao.nyx.azure.translation

import com.larryhsiao.nyx.BuildConfig
import com.larryhsiao.nyx.azure.IssueToken
import com.silverhetch.clotho.source.ConstSource
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test for [com.larryhsiao.nyx.azure.translation.Translation]
 */
@RunWith(RobolectricTestRunner::class)
class TranslationTest {
    /**
     * Check the endpoint exist
     */
    @Test
    fun simple() {
        assertTrue(
            Translation(
                ConstSource(IssueToken(BuildConfig.AZURE_SECRET_KEY).value()),
                arrayOf("This is input"),
                "zh-hant"
            ).value().isNotEmpty()
        )
    }
}