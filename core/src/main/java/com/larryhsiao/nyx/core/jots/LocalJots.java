package com.larryhsiao.nyx.core.jots;

import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.util.List;

/**
 * Local implementation of {@link Jots}.
 */
public class LocalJots implements Jots {
    private final Source<Connection> db;

    public LocalJots(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public List<Jot> all() {
        return new QueriedJots(new AllJots(db, true)).value();
    }

    @Override
    public Jot byId(long id) {
        return new JotById(id, db).value();
    }

    @Override
    public Jot newJot(Jot jot) {
        return new NewJot(db, jot).value();
    }

    @Override
    public void updateJot(Jot jot) {
        new PostedJot(db, jot, true).value();
    }

    @Override
    public void deleteJotById(long id) {
        new JotRemoval(db, id).fire();
    }
}
