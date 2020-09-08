package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Action to update given Jot
 */
public class UpdateJot implements Action {
    private final Jot updated;
    private final Source<Connection> connSource;
    private final boolean increaseVer;

    public UpdateJot(Jot updated, Source<Connection> connSource, boolean increaseVer) {
        this.updated = updated;
        this.connSource = connSource;
        this.increaseVer = increaseVer;
    }

    public UpdateJot(Jot updated, Source<Connection> connSource) {
        this(updated, connSource, true);
    }

    @Override
    public void fire() {
        final Connection conn = connSource.value();
        try (PreparedStatement stmt = conn.prepareStatement(
            // language=H2
            "UPDATE jots " +
                "SET content=?1, location=?2, CREATEDTIME=?3, MOOD=?4, VERSION=?5 , DELETE=?7, TITLE=?8 " +
                "WHERE id=?6;"
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
            stmt.setTimestamp(3, new Timestamp(updated.createdTime()), Calendar.getInstance());
            String mood = updated.mood();
            if (mood.length() > 1) {
                stmt.setString(4, mood.substring(0, 2));
            } else {
                stmt.setString(4, "");
            }
            stmt.setInt(5, increaseVer ? updated.version() + 1 : updated.version());
            stmt.setLong(6, updated.id());
            stmt.setInt(7, updated.deleted() ? 1 : 0);
            stmt.setString(8, updated.title());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
