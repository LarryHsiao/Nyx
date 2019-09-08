package com.larryhsiao.nyx.uri.nyx

import com.larryhsiao.nyx.tag.TagUri
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for [com.larryhsiao.nyx.uri.nyx.TagUri]
 */
class TagUriTest {
    /**
     * Check output
     */
    @Test
    fun simple() {
        assertEquals(
            "nyx://larryhsiao.com/tags/1000",
            TagUri(1000).value().toASCIIString()
        )
    }
}