package com.larryhsiao.nyx.core.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.attachments.AllAttachments;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.QueriedAttachments;
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
 * Take for attachments.
 */
public class TkAttachments implements Take {
    private final Source<Connection> db;

    public TkAttachments(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public Response act(Request req) throws IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Attachment attachment : new QueriedAttachments(new AllAttachments(db)).value()) {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            objBuilder.add("id", attachment.id());
            objBuilder.add("title", attachment.uri());
            arrayBuilder.add(objBuilder.build());
        }
        return new RsJson(arrayBuilder.build());
    }
}
