package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to update exist attachment.
 */
public class UpdateAttachment implements Action {
    private final Source<Connection> db;
    private final Attachment item;
    private final boolean increaseVer;

    public UpdateAttachment(Source<Connection> db, Attachment item) {
        this(db, item, true);
    }

    public UpdateAttachment(Source<Connection> db, Attachment item, boolean increaseVer) {
        this.db = db;
        this.item = item;
        this.increaseVer = increaseVer;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "UPDATE ATTACHMENTS " +
                "SET JOT_ID=?1, VERSION=?2, DELETE = ?3, URI = ?4 " +
                "WHERE ID=?5"
        )) {
            stmt.setLong(1, item.jotId());
            stmt.setInt(2, increaseVer ? item.version() + 1 : item.version());
            stmt.setInt(3, item.deleted() ? 1 : 0);
            stmt.setString(4, item.uri());
            stmt.setLong(5, item.id());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
