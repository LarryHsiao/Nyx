package com.larryhsiao.nyx.core.sync.server.tags;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.sync.json.tags.JotTagsJsonArray;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import java.io.IOException;

/**
 * Take all JotTag.
 */
public class TkJotTags implements Take {
    private final Nyx nyx;

    public TkJotTags(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsJson(
            new JotTagsJsonArray(
                nyx.jotTags().all()
            ).value()
        );
    }
}
