package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All jots in db.
 */
public class AllJots implements Source<ResultSet> {
    private final Source<Connection> conn;
    private final boolean includedDelete;

    public AllJots(Source<Connection> conn, boolean includeDeleted) {
        this.conn = conn;
        this.includedDelete = includeDeleted;
    }

    public AllJots(Source<Connection> conn) {
        this(conn, false);
    }

    @Override
    public ResultSet value() {
        try {
            if (includedDelete) {
                return conn.value().createStatement().executeQuery(
                    // language=H2
                    "SELECT * FROM jots ORDER BY CREATEDTIME DESC;"
                );
            } else {
                return conn.value().createStatement().executeQuery(
                    // language=H2
                    "SELECT * FROM jots WHERE DELETE = 0 ORDER BY CREATEDTIME DESC;"
                );
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
