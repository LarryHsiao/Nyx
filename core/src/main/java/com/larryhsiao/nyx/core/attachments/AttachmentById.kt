package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Source to build {@link Attachment} from given id.
 */
public class AttachmentById implements Source<Attachment> {
    private final Source<Connection> db;
    private final long id;

    public AttachmentById(Source<Connection> db, long id) {
        this.db = db;
        this.id = id;
    }

    @Override
    public Attachment value() {
        try (PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "SELECT * FROM attachments WHERE id=?;"
        )) {
            stmt.setLong(1, id);
            ResultSet res = stmt.executeQuery();
            if (!res.next()) {
                throw new IllegalArgumentException("Jot not found, id: " + id);
            }
            return new ConstAttachment(
                res.getLong("id"),
                res.getLong("jot_id"),
                res.getString("uri"),
                res.getInt("version"),
                res.getInt("delete")
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
