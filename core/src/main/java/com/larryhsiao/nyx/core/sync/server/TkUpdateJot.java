package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.client.JsonJot;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithStatus;

import javax.json.Json;
import java.io.IOException;

/**
 * Take for jot update.
 */
public class TkUpdateJot implements Take {
    private final Nyx nyx;

    public TkUpdateJot(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        nyx.jots().updateJot(
            new JsonJot(Json.createReader(req.body()).readObject())
        );
        return new RsWithStatus(204);
    }
}
