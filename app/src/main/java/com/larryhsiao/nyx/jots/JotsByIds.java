package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Source to query jots by given ids.
 */
public class JotsByIds implements Source<ResultSet> {
    private final Source<Connection> dbSource;
    private final long[] ids;

    public JotsByIds(Source<Connection> dbSource, long[] ids) {
        this.dbSource = dbSource;
        this.ids = ids;
    }

    @Override
    public ResultSet value() {
        try  {
            Statement stmt = dbSource.value().createStatement();
            final StringBuilder idStr = new StringBuilder();
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    idStr.append(", ");
                }
                idStr.append(ids[i]);
            }
            return stmt.executeQuery(
                // language=H2
                "SELECT * FROM jots " +
                    "WHERE ID IN (" + idStr.toString() + ")"
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
