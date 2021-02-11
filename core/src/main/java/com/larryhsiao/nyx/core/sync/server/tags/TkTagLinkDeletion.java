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
 * Take to delete a JotTag link by given JotId and TagId.
 */
public class TkTagLinkDeletion implements Take {
    private final Nyx nyx;

    public TkTagLinkDeletion(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        final JsonObject json = Json.createReader(req.body()).readObject();
        nyx.jotTags().deleteByIds(
            json.getJsonNumber("jot_id").longValue(),
            json.getJsonNumber("tag_id").longValue()
        );
        return new RsWithStatus(204);
    }
}
