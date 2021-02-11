package com.larryhsiao.nyx.core.sync.server.tags;

import com.larryhsiao.nyx.core.Nyx;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithStatus;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;

/**
 * Take the new tag link.
 */
public class TkNewJotTag implements Take {
    private final Nyx nyx;

    public TkNewJotTag(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        final JsonObject json = Json.createReader(req.body()).readObject();
        nyx.jotTags().link(
            json.getJsonNumber("jot_id").longValue(),
            json.getJsonNumber("tag_id").longValue()
        );
        return new RsWithStatus(204);
    }
}
