package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

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

    public NewJot(Source<Connection> db, String content) {
        this.db = db;
        this.content = content;
    }

    @Override
    public Jot value() {
        try (PreparedStatement stmt = db.value().prepareStatement(
                // language=H2
                "INSERT INTO jots(content, createdTime) " +
                        "VALUES (?, ?)",
                RETURN_GENERATED_KEYS
        )) {
            Calendar calendar = Calendar.getInstance();
            stmt.setString(1, content);
            stmt.setTimestamp(2, new Timestamp(calendar.getTimeInMillis()), calendar);
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
