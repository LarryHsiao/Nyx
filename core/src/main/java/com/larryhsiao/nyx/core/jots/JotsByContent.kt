package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to query jots by given ids.
 *
 * @todo #183 Consider to remove this
 */
public class JotsByContent implements Source<ResultSet> {
    private final Source<Connection> dbSource;
    private final String keyword;

    public JotsByContent(Source<Connection> dbSource, String keyword) {
        this.dbSource = dbSource;
        this.keyword = keyword;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = dbSource.value().prepareStatement(
                // language=H2
                "SELECT * FROM jots " +
                    "WHERE UPPER(CONTENT) like UPPER(?) " +
                    "AND DELETE = 0 " +
                    "ORDER BY CREATEDTIME DESC;"
            );
            stmt.setString(1, "%" + keyword + "%");
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
