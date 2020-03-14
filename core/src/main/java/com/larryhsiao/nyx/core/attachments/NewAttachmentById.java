package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * New Attachment of a Jot
 */
public class NewAttachmentById implements Action {
    private final Source<Connection> source;
    private final Attachment item;

    public NewAttachmentById(Source<Connection> source, Attachment item) {
        this.source = source;
        this.item = item;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = source.value().prepareStatement(
            // language=H2
            "INSERT INTO attachments(ID, uri, jot_id, VERSION, DELETE) " +
                "VALUES (?,?,?,?,?)")) {
            stmt.setLong(1, item.id());
            stmt.setString(2, item.uri());
            stmt.setLong(3, item.jotId());
            stmt.setInt(4, item.version());
            stmt.setInt(5, item.deleted() ? 1 : 0);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
