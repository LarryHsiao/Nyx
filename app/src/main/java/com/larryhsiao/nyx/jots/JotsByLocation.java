package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Geometry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Source to find jots by given geometry polygon.
 */
public class JotsByLocation implements Source<ResultSet> {
    private final Source<Connection> conn;
    private final Geometry geometry;

    public JotsByLocation(Source<Connection> conn, Geometry geometry) {
        this.conn = conn;
        this.geometry = geometry;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = conn.value().prepareStatement(
                // language=H2
                "SELECT * FROM jots " +
                    "WHERE location && ?" +
                    "ORDER BY CREATEDTIME DESC;"
            );
            stmt.setString(1, geometry.toText());
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
