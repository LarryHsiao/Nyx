package com.larryhsiao.nyx.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All jots in db.
 */
public class AllTags implements Source<ResultSet> {
    private final Source<Connection> conn;

    public AllTags(Source<Connection> conn) {
        this.conn = conn;
    }

    @Override
    public ResultSet value() {
        try {
            return conn.value().createStatement().executeQuery(
                // language=H2
                "SELECT * FROM TAGS;"
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
