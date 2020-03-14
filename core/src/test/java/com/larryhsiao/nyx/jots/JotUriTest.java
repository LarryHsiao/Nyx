package com.larryhsiao.nyx.jots;

import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.JotUri;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
                    "",
                    1,
                    false)
            ).value().toASCIIString()
        );
    }
}