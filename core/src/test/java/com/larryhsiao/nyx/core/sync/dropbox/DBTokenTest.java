package com.larryhsiao.nyx.core.sync.dropbox;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link DBToken}.
 */
@Disabled
class DBTokenTest {
    /**
     * Check if request success.
     */
    @Test
    void checkIfRequest() {
        final String apiAuth = "";
        final String code = "848T7xnq2m4AAAAAAAAu0IfxPev_Vpu4_wxK0AUcxdQ";
        new DBToken(apiAuth, code).value();
        assertTrue(true);
    }
}