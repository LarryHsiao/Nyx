package com.larryhsiao.nyx.jots;

import com.larryhsiao.nyx.core.jots.JotUriId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit-test for the class {@link JotUriId}
 */
public class JotUriIdTest {

    /**
     * Check the output
     */
    @Test
    public void simple() {
        Assertions.assertEquals(
            1L,
            new JotUriId(
                "http://localhost.com/jots/1"
            ).value().longValue()
        );
    }

    /**
     * Not Jot path
     */
    @Test
    public void notJotPath() {
        try {
            new JotUriId("http://localhost.com/abc/1").value();
            Assertions.fail("Should throw exception");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    /**
     * URI id not a long
     */
    @Test
    public void notId() {
        try {
            new JotUriId("http://localhost.com/jots/number").value();
            Assertions.fail("Should throw exception");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }
}