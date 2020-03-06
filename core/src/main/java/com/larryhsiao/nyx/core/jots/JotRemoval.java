package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to remove a Jot
 */
public class JotRemoval implements Action {
    private final Source<Connection> db;
    private final long id;

    public JotRemoval(Source<Connection> db, long id) {
        this.db = db;
        this.id = id;
    }

    @Override
    public void fire() {
        try {
            final PreparedStatement removeTag = db.value().prepareStatement(
                // language=H2
                "DELETE FROM TAG_JOT " +
                    "WHERE JOT_ID=?"
            );
            removeTag.setLong(1, id);
            removeTag.executeUpdate();
            removeTag.close();

            final PreparedStatement removeJot = db.value().prepareStatement(
                // language=H2
                "DELETE FROM JOTS " +
                    "WHERE ID=?"
            );
            removeJot.setLong(1, id);
            removeJot.executeUpdate();
            removeJot.close();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
