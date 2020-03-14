package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to query attachments by attached Jot id.
 */
public class AttachmentsByJotId implements Source<ResultSet> {
    private final Source<Connection> dbSource;
    private final long jotId;
    private final boolean includeDeleted;

    public AttachmentsByJotId(Source<Connection> dbSource, long jotId, boolean includeDeleted) {
        this.dbSource = dbSource;
        this.jotId = jotId;
        this.includeDeleted = includeDeleted;
    }

    public AttachmentsByJotId(Source<Connection> dbSource, long jotId) {
        this(dbSource, jotId, false);
    }

    @Override
    public ResultSet value() {
        try {
            if (includeDeleted) {
                final PreparedStatement stmt = dbSource.value().prepareStatement(
                    // language=H2
                    "SELECT * FROM attachments " +
                        "WHERE jot_id = ?"
                );
                stmt.setLong(1, jotId);
                return stmt.executeQuery();
            } else {
                final PreparedStatement stmt = dbSource.value().prepareStatement(
                    // language=H2
                    "SELECT * FROM attachments " +
                        "WHERE jot_id = ? " +
                        "AND DELETE = 0;"
                );
                stmt.setLong(1, jotId);
                return stmt.executeQuery();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
