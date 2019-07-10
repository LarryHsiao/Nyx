package com.larryhsiao.nyx.uri

import android.app.Application
import android.content.ContentProvider
import android.content.Context
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Test for com.larryhsiao.nyx.uri.IsInternalFile
 */
@RunWith(RobolectricTestRunner::class)
class IsInternalFileTest{
    /**
     * Check Internal Storage uri
     */
    @Test
    fun simple() {
        assertTrue(
            IsInternalFile(
                ApplicationProvider.getApplicationContext(),
                File(
                    ApplicationProvider.getApplicationContext<Application>().filesDir,
                    "child"
                ).toUri().toString()
            ).value()
        )
    }
}