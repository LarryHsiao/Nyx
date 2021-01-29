package com.larryhsiao.nyx.core.sync.server.jots;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.json.jots.JotsJsonArray;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import java.io.IOException;

/**
 * Take for jots.
 */
public class TkJots implements Take {
    private final Nyx nyx;

    public TkJots(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsJson(
            new JotsJsonArray(
                nyx.jots().all()
            ).value()
        );
    }
}
