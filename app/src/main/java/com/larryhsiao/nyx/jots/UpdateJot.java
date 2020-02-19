package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Action to update given Jot
 */
public class UpdateJot implements Action {
    private final Jot updated;
    private final Source<Connection> connSource;

    public UpdateJot(Jot updated, Source<Connection> connSource) {
        this.updated = updated;
        this.connSource = connSource;
    }

    @Override
    public void fire() {
        final Connection conn = connSource.value();
        try (PreparedStatement stmt = conn.prepareStatement(
            // language=H2
            "UPDATE jots " +
                "SET content=?1, location=?2 " +
                "WHERE id=?3;"
        )) {
            stmt.setString(1, updated.content());
            stmt.setString(2, new Point(
                new CoordinateArraySequence(
                    new Coordinate[]{
                        new Coordinate(
                            updated.location()[0],
                            updated.location()[1]
                        )
                    }
                ), new GeometryFactory()
            ).toText());
            stmt.setLong(3, updated.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
