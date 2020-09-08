package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Geometry;

import java.sql.*;

/**
 * Source to build {@link ResultSet} for querying result by time and geometry.
 */
public class JotsByTimeSpace implements Source<ResultSet> {
    private final Source<Connection> db;
    private final long time;
    private final Source<Geometry> geometry;

    public JotsByTimeSpace(Source<Connection> db, long time, Source<Geometry> geometry) {
        this.db = db;
        this.time = time;
        this.geometry = geometry;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = db.value().prepareStatement(
                // language=H2
                "SELECT * FROM jots " +
                    "WHERE location && ? AND CREATEDTIME > ?" +
                    "AND DELETE = 0 " +
                    "ORDER BY CREATEDTIME DESC;"
            );
            stmt.setString(1, geometry.value().toText());
            stmt.setTimestamp(2, new Timestamp(time - 300000)); // 5 min range
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
