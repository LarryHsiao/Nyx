package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All jots in db.
 */
public class AllJots implements Source<ResultSet> {
    private final Source<Connection> conn;

    public AllJots(Source<Connection> conn) {
        this.conn = conn;
    }

    @Override
    public ResultSet value() {
        try {
            return conn.value().createStatement().executeQuery(
                    // language=H2
                    "SELECT * FROM jots ORDER BY CREATEDTIME DESC;"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
