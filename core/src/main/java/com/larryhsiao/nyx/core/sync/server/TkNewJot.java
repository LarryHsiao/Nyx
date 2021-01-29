package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.NewJot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.sync.client.JsonJot;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;

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
                nyx.jots().newJot(
                    new JsonJot(
                        Json.createReader(req.body()).readObject()
                    )
                ).id()
            ).build()
        );
    }
}
