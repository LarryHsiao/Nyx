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
    private final boolean updateVer;

    public PostedJot(Source<Connection> db, Jot jot, boolean updateVer) {
        this.db = db;
        this.jot = jot;
        this.updateVer = updateVer;
    }

    public PostedJot(Source<Connection> db, Jot jot) {
        this(db, jot, true);
    }

    @Override
    public Jot value() {
        if (jot.id() == -1) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(jot.createdTime()));
            return new NewJot(
                db,
                jot.title(),
                jot.content(),
                jot.location(),
                calendar,
                jot.mood()
            ).value();
        } else {
            new UpdateJot(jot, db, updateVer).fire();
            return jot;
        }
    }
}
