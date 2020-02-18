package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Source to query jots by given ids.
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
                "SELECT * FROM jots " +
                    "WHERE content like ?;"
            );
            stmt.setString(1, "%"+keyword+"%");
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
