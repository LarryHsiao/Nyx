package com.larryhsiao.nyx.core.sync.server.tags;

import com.larryhsiao.nyx.core.Nyx;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithStatus;

import javax.json.Json;
import java.io.IOException;

/**
 * Take to delete tag by id.
 */
public class TkDeleteTag implements Take {
    private final Nyx nyx;

    public TkDeleteTag(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        nyx.tags().deleteById(Json.createReader(req.body())
            .readObject()
            .getJsonNumber("id")
            .longValue());
        return new RsWithStatus(204);
    }
}
