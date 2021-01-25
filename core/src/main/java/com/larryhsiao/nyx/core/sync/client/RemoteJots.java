package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.Jots;

import javax.json.Json;
import java.util.List;

/**
 * Jots from remote server via http.
 */
public class RemoteJots implements Jots {
    private final String host;

    public RemoteJots(String host) {
        this.host = host;
    }

    @Override
    public List<Jot> all() {
        return new JsonJots(
            Json.createParser(new AllJots(
                host
            ).value()).getArray()
        ).value();
    }
}
