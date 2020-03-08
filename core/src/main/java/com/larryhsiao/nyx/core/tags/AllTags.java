package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All jots in db.
 */
public class AllTags implements Source<ResultSet> {
    private final Source<Connection> conn;
    private final boolean icnludeDeleted;

    public AllTags(Source<Connection> conn, boolean icnludeDeleted) {
        this.conn = conn;
        this.icnludeDeleted = icnludeDeleted;
    }

    public AllTags(Source<Connection> conn) {
        this(conn, false);
    }

    @Override
    public ResultSet value() {
        try {
            if (icnludeDeleted){
                return conn.value().createStatement().executeQuery(
                    // language=H2
                    "SELECT * FROM TAGS;"
                );
            }else {
                return conn.value().createStatement().executeQuery(
                    // language=H2
                    "SELECT * FROM TAGS WHERE DELETE = 0;"
                );
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
