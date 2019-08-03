package com.larryhsiao.nyx.uri.nyx

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for [com.larryhsiao.nyx.uri.nyx.NyxUri]
 */
class NyxUriTest {
    /**
     * Check result of uri
     */
    @Test
    fun simple() {
        assertEquals(
            "nyx://larryhsiao.com/abc",
            NyxUri("/abc").value().toASCIIString()
        )
    }
}