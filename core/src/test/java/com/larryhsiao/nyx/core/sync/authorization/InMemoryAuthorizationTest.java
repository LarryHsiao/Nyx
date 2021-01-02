package com.larryhsiao.nyx.core.sync.authorization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Test for {@link InMemoryAuthorization}.
 */
class InMemoryAuthorizationTest {
    /**
     * Total process of success authorization.
     */
    @Test
    void pairingSuccess() {
        final String guid = UUID.randomUUID().toString();
        final Authorization auth = new InMemoryAuthorization();
        final String requestCode = auth.requestPair(guid);
        auth.confirmPairing(guid, requestCode);
        Assertions.assertTrue(auth.authorizedIds().contains(guid));
    }

    /**
     * Authorization failure when confirm with wrong request code.
     */
    @Test
    void pairingFailed() {
        final String guid = UUID.randomUUID().toString();
        final Authorization auth = new InMemoryAuthorization();
        auth.requestPair(guid);
        auth.confirmPairing(guid, "");
        Assertions.assertFalse(auth.authorizedIds().contains(guid));
    }
}