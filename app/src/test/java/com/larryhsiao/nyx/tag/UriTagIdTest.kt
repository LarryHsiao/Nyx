package com.larryhsiao.nyx.tag

import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalArgumentException
import java.net.URI

/**
 * Tests for [com.larryhsiao.nyx.tag.UriTagId]
 */
class UriTagIdTest {

    /**
     * Normal case`s result
     */
    @Test
    fun normalCase() {
        assertEquals(
            1000,
            UriTagId(
                URI("nyx://larryhsiao.com/tags/1000")
            ).value()
        )
    }

    /**
     * Check there is a exception if uri not a tag uri
     */
    @Test
    fun noTagUri() {
        try {
            UriTagId(
                URI("nyx://larryhsiao.com/abccc/1000")
            ).value()
            fail()
        } catch (e: IllegalArgumentException) {
            assertTrue(true)
        }

    }
}