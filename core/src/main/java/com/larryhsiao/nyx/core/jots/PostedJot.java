package com.larryhsiao.nyx.core.jots;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

/**
 * Source to build a Jot that just updated or created.
 */
public class PostedJot implements Source<Jot> {
    private final Source<Connection> db;
    private final Jot jot;

    public PostedJot(Source<Connection> db, Jot jot) {
        this.db = db;
        this.jot = jot;
    }

    @Override
    public Jot value() {
        if (jot.id() == -1) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(jot.createdTime()));
            return new NewJot(db, jot.content(), jot.location(), calendar, jot.mood()).value();
        } else {
            new UpdateJot(jot, db).fire();
            return jot;
        }
    }
}
