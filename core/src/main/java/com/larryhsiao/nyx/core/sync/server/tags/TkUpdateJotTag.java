package com.larryhsiao.nyx.core.sync.server.tags;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.json.tags.JsonJotTag;
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
public class TkUpdateJotTag implements Take {
    private final Nyx nyx;

    public TkUpdateJotTag(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        nyx.jotTags().update(
            new JsonJotTag(Json.createReader(req.body()).readObject())
        );
        return new RsWithStatus(204);
    }
}
