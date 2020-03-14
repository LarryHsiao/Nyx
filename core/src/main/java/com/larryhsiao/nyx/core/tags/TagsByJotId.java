package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to build tags by Jot id.
 */
public class TagsByJotId implements Source<ResultSet> {
    private final Source<Connection> connSource;
    private final long jotId;
    private final boolean includingDeleted;

    public TagsByJotId(Source<Connection> connSource, long jotId) {
        this(connSource, jotId, false);
    }

    public TagsByJotId(Source<Connection> connSource, long jotId, boolean includingDeleted) {
        this.connSource = connSource;
        this.jotId = jotId;
        this.includingDeleted = includingDeleted;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt;
            if (includingDeleted) {
                stmt = connSource.value().prepareStatement(
                    //language=H2
                    "SELECT  * FROM TAGS " +
                        "INNER JOIN TAG_JOT TJ on TAGS.ID = TJ.TAG_ID " +
                        "WHERE TJ.JOT_ID=?;"
                );
            } else {
                stmt = connSource.value().prepareStatement(
                    //language=H2
                    "SELECT  * FROM TAGS " +
                        "INNER JOIN TAG_JOT TJ on TAGS.ID = TJ.TAG_ID " +
                        "WHERE TJ.JOT_ID=? AND TJ.DELETE = 0 AND TAGS.DELETE=0;"
                );
            }
            stmt.setLong(1, jotId);
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
