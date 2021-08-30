package com.larryhsiao.nyx.core.jots;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.Source;
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
 * Action to dump a {@link Jot} into database.
 */
public class DumpJot implements Action {
    private final Source<Connection> db;
    private final Jot jot;

    public DumpJot(Source<Connection> db, Jot jot) {
        this.db = db;
        this.jot = jot;
    }

    @Override
    public void fire() {
        try (final PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "INSERT INTO jots(ID, content, createdTime, location, mood, VERSION, TITLE, PRIVATE, delete)\n" +
                "VALUES (?9, ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)"
        )) {
            stmt.setString(1, jot.content());
            stmt.setTimestamp(2, new Timestamp(jot.createdTime()), Calendar.getInstance());
            stmt.setString(3, new Point(
                new CoordinateArraySequence(new Coordinate[]{
                    new Coordinate(jot.location()[0], jot.location()[1])
                }), new GeometryFactory()
            ).toText());
            if (jot.mood().length() > 1) {
                stmt.setString(4, jot.mood().substring(0, 2));
            } else {
                stmt.setString(4, "");
            }
            stmt.setInt(5, jot.version());
            stmt.setString(6, jot.title());
            stmt.setBoolean(7, jot.privateLock());
            stmt.setBoolean(8, jot.deleted());
            stmt.setLong(9, jot.id());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Insert failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
