package com.larryhsiao.nyx.core.sync.authorization;

import java.util.*;

/**
 * In-memory implementation of {@link Authorization}.
 */
public class InMemoryAuthorization implements Authorization {
    private final Set<String> authorizedIds = new HashSet<>();
    private final Map<String, String> pairing = new HashMap<>();

    @Override
    public Set<String> authorizedIds() {
        return authorizedIds;
    }

    @Override
    public Map<String, String> pairingIds() {
        return pairing;
    }

    @Override
    public String requestPair(String guid) {
        final String requestCode = UUID.randomUUID().toString();
        pairing.put(guid, requestCode);
        return requestCode;
    }

    @Override
    public boolean confirmPairing(String guid, String code) {
        String paringCode = pairing.get(guid);
        if (paringCode != null && paringCode.equals(code)) {
            authorizedIds.add(guid);
            return true;
        } else {
            return false;
        }
    }
}
