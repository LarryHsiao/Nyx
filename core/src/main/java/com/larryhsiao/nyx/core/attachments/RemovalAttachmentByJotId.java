package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Remove Attachments by given Jot id.
 */
public class RemovalAttachmentByJotId implements Action {
    private final Source<Connection> dbConn;
    private final long jotId;

    public RemovalAttachmentByJotId(Source<Connection> dbConn, long jotId) {
        this.dbConn = dbConn;
        this.jotId = jotId;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = dbConn.value().prepareStatement(
            // language=H2
            "DELETE FROM attachments " +
                "WHERE JOT_ID=?;"
        )) {
            stmt.setLong(1, jotId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
