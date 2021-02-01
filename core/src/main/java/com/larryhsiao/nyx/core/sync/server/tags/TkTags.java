package com.larryhsiao.nyx.core.sync.server.tags;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.json.tags.TagsJsonArray;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import java.io.IOException;

/**
 * Take all tags.
 */
public class TkTags implements Take {
    private final Nyx nyx;

    public TkTags(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsJson(
            new TagsJsonArray(
                nyx.tags().all()
            ).value()
        );
    }
}
