package com.larryhsiao.nyx.core.sync.server.tags;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.json.jots.JsonJot;
import com.larryhsiao.nyx.core.sync.json.tags.JsonTag;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithStatus;

import javax.json.Json;
import java.io.IOException;

/**
 * Take for tag update.
 */
public class TkUpdateTag implements Take {
    private final Nyx nyx;

    public TkUpdateTag(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        nyx.tags().update(
            new JsonTag(Json.createReader(req.body()).readObject())
        );
        return new RsWithStatus(204);
    }
}
