package com.larryhsiao.nyx.core.sync.server.jots;

import com.larryhsiao.nyx.core.Nyx;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithStatus;

import javax.json.Json;
import java.io.IOException;

public class TkDeleteJot implements Take {
    private final Nyx nyx;

    public TkDeleteJot(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        nyx.jots().deleteById(Json.createReader(req.body())
            .readObject()
            .getJsonNumber("id")
            .longValue());
        return new RsWithStatus(204);
    }
}
