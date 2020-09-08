package com.larryhsiao.nyx.core.jots

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Unit-test for the class [JotUriId]
 */
class JotUriIdTest {
    /**
     * Check the output
     */
    @Test
    fun simple() {
        Assertions.assertEquals(
            1L,
            JotUriId(
                "http://localhost.com/jots/1"
            ).value()
        )
    }

    /**
     * Not Jot path
     */
    @Test
    fun notJotPath() {
        try {
            JotUriId("http://localhost.com/abc/1").value()
            Assertions.fail<Any>("Should throw exception")
        } catch (e: Exception) {
            Assertions.assertTrue(true)
        }
    }

    /**
     * URI id not a long
     */
    @Test
    fun notId() {
        try {
            JotUriId("http://localhost.com/jots/number").value()
            Assertions.fail<Any>("Should throw exception")
        } catch (e: Exception) {
            Assertions.assertTrue(true)
        }
    }
}