package com.larryhsiao.nyx.jots;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit-test for the class {@link JotUriId}
 */
public class JotUriIdTest {

    /**
     * Check the output
     */
    @Test
    public void simple() {
        Assert.assertEquals(
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
            Assert.fail("Should throw exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * URI id not a long
     */
    @Test
    public void notId() {
        try {
            new JotUriId("http://localhost.com/jots/number").value();
            Assert.fail("Should throw exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }
}