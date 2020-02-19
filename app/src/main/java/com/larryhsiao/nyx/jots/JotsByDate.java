package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to build jot by given date.
 */
public class JotsByDate implements Source<ResultSet> {
    private final Date date;
    private final Source<Connection> db;

    public JotsByDate(Date date, Source<Connection> db) {
        this.date = date;
        this.db = db;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = db.value().prepareStatement(
                // language=H2
                "SELECT * FROM jots " +
                    "WHERE CAST(CREATEDTIME AS DATE) = ?;"
            );
            stmt.setDate(1, date);
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
