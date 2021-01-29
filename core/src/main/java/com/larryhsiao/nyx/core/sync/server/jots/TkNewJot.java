package com.larryhsiao.nyx.core.sync.server.jots;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.json.jots.JsonJot;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import javax.json.Json;
import java.io.IOException;

/**
 * Take for jots.
 */
public class TkNewJot implements Take {
    private final Nyx nyx;

    public TkNewJot(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsJson(
            Json.createObjectBuilder().add(
                "id",
                nyx.jots().create(
                    new JsonJot(
                        Json.createReader(req.body()).readObject()
                    )
                ).id()
            ).build()
        );
    }
}
