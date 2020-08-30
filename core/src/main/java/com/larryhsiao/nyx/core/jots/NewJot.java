package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.sql.*;
import java.util.Calendar;

import static java.lang.Double.MIN_VALUE;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * Source to build a Jot which just created by user.
 */
public class NewJot implements Source<Jot> {
    private final Source<Connection> db;
    private final Jot jot;

    public NewJot(
        Source<Connection> db,
        String title,
        String content) {
        this(db, title, content, new double[]{MIN_VALUE, MIN_VALUE}, Calendar.getInstance(), " ");
    }

    public NewJot(
        Source<Connection> db,
        String title,
        String content,
        Calendar calendar,
        String mood) {
        this(db, title, content, new double[]{MIN_VALUE, MIN_VALUE}, calendar, mood);
    }

    public NewJot(
        Source<Connection> db,
        String title,
        String content,
        double[] location,
        String mood) {
        this(db, title, content, location, Calendar.getInstance(), mood);
    }

    public NewJot(
        Source<Connection> db,
        String title,
        String content,
        double[] location,
        Calendar calendar,
        String mood) {
        this.db = db;
        this.jot = new ConstJot(
            -1L,
            title,
            content,
            calendar.getTimeInMillis(),
            location,
            mood,
            1,
            false
        );
    }

    public NewJot(Source<Connection> db, Jot jot) {
        this.db = db;
        this.jot = jot;
    }

    @Override
    public Jot value() {
        try (PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "INSERT INTO jots(content, createdTime, location, mood, VERSION) " +
                "VALUES (?, ?, ?, ?, ?)",
            RETURN_GENERATED_KEYS
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
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Insert failed");
            }
            final ResultSet res = stmt.getGeneratedKeys();
            if (!res.next()) {
                throw new IllegalArgumentException("Create jot failed: " + jot.content());
            }
            long newId = res.getLong(1);
            return new WrappedJot(jot) {
                @Override
                public long id() {
                    return newId;
                }
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
