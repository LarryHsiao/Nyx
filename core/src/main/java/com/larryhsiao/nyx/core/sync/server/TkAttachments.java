package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.nyx.core.Nyx;
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

/**
 * Take for attachments.
 */
public class TkAttachments implements Take {
    private final Nyx nyx;

    public TkAttachments(Nyx nyx) {
        this.nyx = nyx;
    }

    @Override
    public Response act(Request req) throws IOException {
        // @todo #105 Pull the json building process to object.
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
//        for (Attachment attachment : new QueriedAttachments(new AllAttachments(db)).value()) {
//            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
//            objBuilder.add("id", attachment.id());
//            objBuilder.add("title", attachment.uri());
//            arrayBuilder.add(objBuilder.build());
//        }
        return new RsJson(arrayBuilder.build());
    }
}
