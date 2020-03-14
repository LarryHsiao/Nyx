package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Remove Attachments by given Jot id.
 */
public class RemovalAttachment implements Action {
    private final Source<Connection> dbConn;
    private final long id;

    public RemovalAttachment(Source<Connection> dbConn, long id) {
        this.dbConn = dbConn;
        this.id = id;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = dbConn.value().prepareStatement(
            // language=H2
            "UPDATE attachments " +
                "SET DELETE = 1 , VERSION = VERSION + 1 " +
                "WHERE ID=?1;"
        )) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
