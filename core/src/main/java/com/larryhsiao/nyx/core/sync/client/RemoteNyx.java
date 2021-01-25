package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jots;

/**
 * Remote implementation of {@link Nyx}.
 * Which fetch the Jots from another Nyx application.
 */
public class RemoteNyx implements Nyx {
    private final String host;

    public RemoteNyx(String host) {
        this.host = host;
    }

    @Override
    public Jots jots() {
        return new RemoteJots(host);
    }
}
