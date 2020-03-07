package com.larryhsiao.nyx.core.jots;

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
 * Source to build a Jot which just created with specific Id.
 */
public class NewJotById implements Source<Jot> {
    private final Source<Connection> db;
    private final Jot jot;

    public NewJotById(Source<Connection> db, Jot jot) {
        this.db = db;
        this.jot = jot;
    }

    @Override
    public Jot value() {
        try (PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "INSERT INTO jots(content, createdTime, location, mood, VERSION, ID, DELETE) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
        )) {
            stmt.setString(1, jot.content());
            stmt.setTimestamp(2, new Timestamp(jot.createdTime()), Calendar.getInstance());
            double[] location = jot.location();
            if (location == null) {
                stmt.setString(3, null);
            } else {
                stmt.setString(3, new Point(
                    new CoordinateArraySequence(
                        new Coordinate[]{
                            new Coordinate(location[0], location[1])
                        }
                    ), new GeometryFactory()
                ).toText());
            }
            if (jot.mood().length() > 1) {
                stmt.setString(4, jot.mood().substring(0, 2));
            } else {
                stmt.setString(4, "");
            }
            stmt.setInt(5, jot.version());
            stmt.setLong(6, jot.id());
            stmt.setInt(7, jot.deleted() ? 1 : 0);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Insert failed");
            }
            return jot;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
