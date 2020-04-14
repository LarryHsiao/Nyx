package com.larryhsiao.nyx.core.web;

import com.google.gson.Gson;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.silverhetch.clotho.Source;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;
import org.takes.rs.RsWithType;

import java.io.IOException;
import java.sql.Connection;

/**
 * Take jots.
 */
public class TkJots implements Take {
    private final Gson gson;
    private final Source<Connection> db;

    public TkJots(Gson gson, Source<Connection> db) {
        this.gson = gson;
        this.db = db;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsWithType(
            new RsText(
                new Gson().toJson(
                    new QueriedJots(new AllJots(db)).value()
                )
            ), "application/json");
    }
}
