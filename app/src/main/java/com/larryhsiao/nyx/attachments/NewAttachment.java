package com.larryhsiao.nyx.attachments;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * New Attachment of a Jot
 */
public class NewAttachment implements Source<Attachment> {
    private final Source<Connection> source;
    private final String uri;
    private final long jotId;

    public NewAttachment(Source<Connection> source, String uri, long jotId) {
        this.source = source;
        this.uri = uri;
        this.jotId = jotId;
    }

    @Override
    public Attachment value() {
        try (PreparedStatement stmt = source.value().prepareStatement(
            // language=H2
            "INSERT INTO attachments(uri, jot_id) " +
                "VALUES (?,?)",
            RETURN_GENERATED_KEYS
        )) {
            stmt.setString(1, uri);
            stmt.setLong(2, jotId);
            stmt.executeUpdate();
            final ResultSet res = stmt.getGeneratedKeys();
            if (!res.next()) {
                throw new IllegalArgumentException("Creating Attachment failed, jotId: "+ jotId+", Uri: "+ uri);
            }
            return new ConstAttachment(res.getLong(1), jotId, uri);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
