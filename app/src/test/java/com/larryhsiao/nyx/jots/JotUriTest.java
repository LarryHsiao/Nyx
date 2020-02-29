package com.larryhsiao.nyx.jots;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit-test for the class {@link JotUri}
 */
public class JotUriTest {
    /**
     * Check simple output.
     */
    @Test
    public void simple() {
        assertEquals(
            "http://localhost.com/jots/1",
            new JotUri(
                "http://localhost.com/",
                new ConstJot(
                    1,
                    "content",
                    0,
                    new double[]{},
                    ' '
                )
            ).value().toASCIIString()
        );
    }
}