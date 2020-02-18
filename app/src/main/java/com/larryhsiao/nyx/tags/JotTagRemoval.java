package com.larryhsiao.nyx.tags;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to remove attached tag from jot.
 */
public class JotTagRemoval implements Action {
    private final Source<Connection> conn;
    private final long jotId;

    public JotTagRemoval(Source<Connection> conn, long jotId) {
        this.conn = conn;
        this.jotId = jotId;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = conn.value().prepareStatement(
            // language=H2
            "DELETE FROM tag_jot WHERE jot_id=?"
        )) {
            stmt.setLong(1, jotId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
