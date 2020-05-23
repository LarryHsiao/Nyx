package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to build {@link ResultSet} that search Jots by keyword.
 * <p>
 * Search scope:
 * - Jot content
 * - Tag name
 */
public class JotsByKeyword implements Source<ResultSet> {
    private final Source<Connection> dbSource;
    private final String keyword;

    public JotsByKeyword(Source<Connection> dbSource, String keyword) {
        this.dbSource = dbSource;
        this.keyword = keyword;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = dbSource.value().prepareStatement(
                // language=H2
                "SELECT JOTS.* FROM jots " +
                    "LEFT JOIN TAG_JOT ON TAG_JOT.JOT_ID = JOTS.ID " +
                    "LEFT JOIN TAGS ON TAG_JOT.TAG_ID = TAGS.ID " +
                    "WHERE (UPPER(JOTS.content) like UPPER(?) OR UPPER(TAGS.TITLE) like UPPER(?))" +
                    "AND JOTS.DELETE = 0 " +
                    "GROUP BY JOTS.ID, JOTS.CREATEDTIME " +
                    "ORDER BY JOTS.CREATEDTIME DESC;"
            );
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
