package com.larryhsiao.nyx.attachments;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Create multiple attachments
 */
public class NewAttachments implements Source<List<Attachment>> {
    private final Source<Connection> connSource;
    private final long jotId;
    private final String[] uris;

    public NewAttachments(Source<Connection> connSource, long jotId, String[] uris) {
        this.connSource = connSource;
        this.jotId = jotId;
        this.uris = uris;
    }

    @Override
    public List<Attachment> value() {
        List<Attachment> res = new ArrayList<>();
        for (String uri : uris) {
            res.add(new NewAttachment(
                connSource,
                uri,
                jotId
            ).value());
        }
        return res;
    }
}
