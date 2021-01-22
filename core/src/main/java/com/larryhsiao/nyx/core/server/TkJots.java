package com.larryhsiao.nyx.core.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
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
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Jot jot : new QueriedJots(new AllJots(db)).value()) {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            objBuilder.add("id", jot.id());
            objBuilder.add("title", jot.title());
            arrayBuilder.add(objBuilder.build());
        }
        return new RsJson(arrayBuilder.build());
    }
}
