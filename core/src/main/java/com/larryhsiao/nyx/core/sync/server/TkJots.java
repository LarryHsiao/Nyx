package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import java.io.IOException;
import java.sql.Connection;

/**
 * Take for jots.
 */
public class TkJots implements Take {
    private final Source<Connection> db;

    public TkJots(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsJson(
            new JotsJsonArray(new QueriedJots(new AllJots(db)).value()).value()
        );
    }
}
