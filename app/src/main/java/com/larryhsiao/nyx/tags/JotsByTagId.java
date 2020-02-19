package com.larryhsiao.nyx.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to build a query of Jots by Tag id.
 */
public class JotsByTagId implements Source<ResultSet> {
    private final Source<Connection> connSource;
    private final Source<Long> tagId;

    public JotsByTagId(Source<Connection> connSource, Source<Long> tagId) {
        this.connSource = connSource;
        this.tagId = tagId;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = connSource.value().prepareStatement(
                // language=H2
                "SELECT * FROM jots " +
                    "INNER JOIN TAG_JOT ON JOTS.ID=TAG_JOT.JOT_ID " +
                    "WHERE TAG_ID=?;"
            );
            stmt.setLong(1, tagId.value());
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
