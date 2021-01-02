package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.sync.network.PingResponse;

/**
 * A remote response to a ping.
 */
public class PingedRemote implements NyxRemote {
    private final PingResponse pingResponse;

    public PingedRemote(PingResponse pingResponse) {
        this.pingResponse = pingResponse;
    }

    @Override
    public String name() {
        return pingResponse.name;
    }

    @Override
    public boolean isPaired() {
        return false;
    }
}
