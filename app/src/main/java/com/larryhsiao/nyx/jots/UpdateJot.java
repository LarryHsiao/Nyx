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
import java.sql.Timestamp;
import java.util.Calendar;

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
                "SET content=?1, location=?2, CREATEDTIME=?3, MOOD=?4 " +
                "WHERE id=?5;"
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
            if (mood.length()>1){
                stmt.setString(4, mood.substring(0,2));
            }else{
                stmt.setString(4, "");
            }
            stmt.setLong(5, updated.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
