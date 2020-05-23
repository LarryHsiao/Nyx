package com.larryhsiao.nyx.core.jots;

import com.larryhsiao.nyx.core.jots.filter.Filter;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to build a {@link ResultSet}.
 * Note: The filter should have all field filled up to prevent unexpected result.
 * In that case, use {@link JotsByCheckedFilter}.
 */
public class JotsByFilter implements Source<ResultSet> {
    private final Source<Connection> dbSource;
    private final Filter filter;

    public JotsByFilter(Source<Connection> dbSource, Filter filter) {
        this.dbSource = dbSource;
        this.filter = filter;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = dbSource.value().prepareStatement(
                // language=H2
                "SELECT JOTS.* FROM jots " +
                    "LEFT JOIN TAG_JOT ON TAG_JOT.JOT_ID = JOTS.ID " +
                    "LEFT JOIN TAGS ON TAG_JOT.TAG_ID = TAGS.ID " +
                    "WHERE CAST(JOTS.CREATEDTIME AS DATE) >= ? " +
                    "AND CAST(JOTS.CREATEDTIME AS DATE) <= ? " +
                    "AND JOTS.DELETE = 0 " +
                    "AND (UPPER(JOTS.content) like UPPER(?) OR UPPER(TAGS.TITLE) like UPPER(?))" +
                    "GROUP BY JOTS.ID, JOTS.CREATEDTIME " +
                    "ORDER BY JOTS.CREATEDTIME DESC;"
            );
            Date started = new Date(filter.dateRange()[0]);
            Date ended = new Date(filter.dateRange()[1]);
            stmt.setDate(1, started);
            stmt.setDate(2, ended);
            stmt.setString(3, "%" + filter.keyword() + "%");
            stmt.setString(4, "%" + filter.keyword() + "%");
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
