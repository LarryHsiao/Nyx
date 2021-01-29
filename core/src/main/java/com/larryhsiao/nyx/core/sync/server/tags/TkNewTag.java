package com.larryhsiao.nyx.core.sync.server.tags;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.json.tags.JsonTag;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import javax.json.Json;
import java.io.IOException;

/**
 * Take implementation for creating new Tag.
 */
public class TkNewTag implements Take {
    private final Nyx nyx;

    public TkNewTag(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsJson(
            Json.createObjectBuilder().add(
                "id",
                nyx.tags().newTag(
                    new JsonTag(Json.createReader(req.body()).readObject())
                ).id()
            ).build()
        );
    }
}
