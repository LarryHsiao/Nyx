package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;
import org.locationtech.jts.geom.Geometry;

import java.sql.*;
import java.util.Calendar;
import java.util.TimeZone;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * Source to build a Jot which just created
 */
public class NewJot implements Source<Jot> {
    private final Source<Connection> db;
    private final String content;
    private final Geometry geometry;

    public NewJot(Source<Connection> db, String content){
        this(db, content, null);
    }

    public NewJot(Source<Connection> db, String content, Geometry geometry) {
        this.db = db;
        this.content = content;
        this.geometry = geometry;
    }

    @Override
    public Jot value() {
        try (PreparedStatement stmt = db.value().prepareStatement(
                // language=H2
                "INSERT INTO jots(content, createdTime, location) " +
                        "VALUES (?, ?, ?)",
                RETURN_GENERATED_KEYS
        )) {
            Calendar calendar = Calendar.getInstance();
            stmt.setString(1, content);
            stmt.setTimestamp(2, new Timestamp(calendar.getTimeInMillis()), calendar);
            if (geometry == null) {
                stmt.setString(3,null);
            }else{
                stmt.setString(3, geometry.toText());
            }
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Insert failed");
            }
            final ResultSet res = stmt.getGeneratedKeys();
            res.next();
            return new ConstJot(
                    res.getInt(1),
                    content,
                    calendar.getTimeInMillis()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
