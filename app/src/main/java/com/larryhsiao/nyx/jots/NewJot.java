package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.source.ConstSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.sql.*;
import java.util.Calendar;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * Source to build a Jot which just created
 */
public class NewJot implements Source<Jot> {
    private final Source<Connection> db;
    private final String content;
    private final double[] location;

    public NewJot(Source<Connection> db, String content) {
        this(db, content, null);
    }

    public NewJot(Source<Connection> db, String content, double[] location) {
        this.db = db;
        this.content = content;
        this.location = location;
    }

    @Override
    public Jot value() {
        try (PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "INSERT INTO jots(content, createdTime, location) " +
                "VALUES (?, ?, ?)",
            RETURN_GENERATED_KEYS
        )) {
            final Calendar calendar = Calendar.getInstance();
            stmt.setString(1, content);
            stmt.setTimestamp(2, new Timestamp(calendar.getTimeInMillis()), calendar);
            if (this.location == null) {
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
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Insert failed");
            }
            final ResultSet res = stmt.getGeneratedKeys();
            res.next();
            return new ConstJot(
                res.getInt(1),
                content,
                calendar.getTimeInMillis(),
                new ConstSource<>(location));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
