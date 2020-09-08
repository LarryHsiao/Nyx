package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to remove attached a tag from jot.
 */
public class JotTagRemoval implements Action {
    private final Source<Connection> conn;
    private final long jotId;
    private final long tagId;

    public JotTagRemoval(Source<Connection> conn, long jotId, long tagId) {
        this.conn = conn;
        this.jotId = jotId;
        this.tagId = tagId;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = conn.value().prepareStatement(
            // language=H2
            "UPDATE tag_jot " +
                "SET DELETE = 1,  VERSION = VERSION + 1" +
                " WHERE jot_id=? AND TAG_ID=?"
        )) {
            stmt.setLong(1, jotId);
            stmt.setLong(2, tagId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
