package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to remove
 */
public class TagRemoval implements Action {
    private final Source<Connection> db;
    private final long id;

    public TagRemoval(Source<Connection> db, long id) {
        this.db = db;
        this.id = id;
    }

    @Override
    public void fire() {
        try {
            final PreparedStatement linkRemoval = db.value().prepareStatement(
                // language=H2
                "UPDATE TAG_JOT " +
                    "SET DELETE = 1, VERSION = VERSION + 1 " +
                    "WHERE TAG_ID=?;"
            );
            linkRemoval.setLong(1, id);
            linkRemoval.executeUpdate();
            linkRemoval.close();
            final PreparedStatement tagRemoval = db.value().prepareStatement(
                // language=H2
                "UPDATE tags " +
                    "SET DELETE = 1, VERSION = VERSION + 1 " +
                    "WHERE id=?;"
            );
            tagRemoval.setLong(1, id);
            tagRemoval.executeUpdate();
            tagRemoval.close();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
