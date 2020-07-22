package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Source to build jots query which have attachment.
 */
public class JotsWithAttachment implements Source<ResultSet> {
    private final Source<Connection> conn;

    public JotsWithAttachment(Source<Connection> conn) {
        this.conn = conn;
    }

    @Override
    public ResultSet value() {
        try {
            return conn.value().createStatement().executeQuery(
                // language=H2
                "SELECT jots.* FROM jots " +
                    "INNER JOIN ATTACHMENTS ON ATTACHMENTS.JOT_ID=jots.ID " +
                    "WHERE jots.DELETE = 0 AND ATTACHMENTS.DELETE = 0 " +
                    "GROUP BY jots.ID, CREATEDTIME " +
                    "ORDER BY CREATEDTIME DESC;"
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
