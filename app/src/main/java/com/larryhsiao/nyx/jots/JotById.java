package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Source to build jot by Id.
 */
public class JotById implements Source<Jot> {
    private final Long id;
    private final Source<Connection> db;

    public JotById(Long id, Source<Connection> db) {
        this.id = id;
        this.db = db;
    }

    @Override
    public Jot value() {
        try (PreparedStatement stmt = db.value().prepareStatement(
                // language=H2
                "SELECT * FROM jots WHERE id=?;"
        )) {
            stmt.setLong(1, id);
            ResultSet res = stmt.executeQuery();
            res.next();
            return new ConstJot(
                    res.getLong("id"),
                    res.getString("content"),
                    res.getTimestamp(
                            res.findColumn("createdTime"),
                            Calendar.getInstance()
                    ).getTime()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
