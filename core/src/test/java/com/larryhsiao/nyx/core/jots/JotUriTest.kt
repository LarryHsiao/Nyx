package com.larryhsiao.nyx.core.jots

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Unit-test for the class [JotUri]
 */
class JotUriTest {
    /**
     * Check simple output.
     */
    @Test
    fun simple() {
        Assertions.assertEquals(
            "http://localhost.com/jots/1",
            JotUri(
                "http://localhost.com/",
                ConstJot(
                    1,
                    "",
                    "content",
                    0, doubleArrayOf(),
                    "",
                    1,
                    false)
            ).value().toASCIIString()
        )
    }
}