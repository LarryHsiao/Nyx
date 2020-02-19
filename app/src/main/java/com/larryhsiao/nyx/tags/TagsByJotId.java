package com.larryhsiao.nyx.tags;

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

    public TagsByJotId(Source<Connection> connSource, long jotId) {
        this.connSource = connSource;
        this.jotId = jotId;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = connSource.value().prepareStatement(
                //language=H2
                "SELECT  * FROM TAGS " +
                    "INNER JOIN TAG_JOT TJ on TAGS.ID = TJ.TAG_ID " +
                    "WHERE TJ.JOT_ID=?;"
            );
            stmt.setLong(1, jotId);
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
