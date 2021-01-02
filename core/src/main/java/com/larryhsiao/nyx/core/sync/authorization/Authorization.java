package com.larryhsiao.nyx.core.sync.authorization;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Authorization for the remote devices.
 */
public interface Authorization {
    /**
     * @return Authorized ids.
     */
    Set<String> authorizedIds();

    /**
     * @return Key, Value: Guid, request code
     */
    Map<String, String> pairingIds();

    /**
     * @param guid The Guid requesting the pairing.
     * @return The request code of paring, the client should use this code to verify the pairing.
     */
    String requestPair(String guid);

    /**
     * @param guid Guid of client.
     * @param code Request code for verification which is fetch from {@link #requestPair(String)}
     *             ad remote device.
     * @return Success or not.
     */
    boolean confirmPairing(String guid, String code);
}
