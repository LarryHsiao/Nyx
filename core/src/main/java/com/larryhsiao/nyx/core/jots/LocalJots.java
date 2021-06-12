package com.larryhsiao.nyx.core.jots;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.attachments.RemovalAttachmentByJotId;
import com.larryhsiao.nyx.core.metadata.MetadataDeletionById;
import com.larryhsiao.nyx.core.metadata.MetadataDeletionByJotId;

import java.sql.Connection;
import java.util.Calendar;
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
    public Jot create(Jot jot) {
        return new NewJot(db, jot).value();
    }

    @Override
    public Jot update(Jot jot) {
        return new PostedJot(db, jot, true).value();
    }

    @Override
    public void deleteById(long id) {
        new JotRemoval(db, id).fire();
        new RemovalAttachmentByJotId(db, id).fire();
        new MetadataDeletionByJotId(db, id).fire();
    }

    @Override
    public List<Jot> byDateRange(Calendar from, Calendar to) {
        return new QueriedJots(new JotsByDateRange(db, from, to)).value();
    }

    @Override
    public Jot createByTimeSpace(Calendar time, double[] longLat, int offset) {
        return new PostedJotByTimeRange(
            db,
            time,
            longLat,
            offset
        ).value();
    }

    @Override
    public List<Jot> byKeyword(String keyword) {
        return new QueriedJots(new JotsByKeyword(db, keyword)).value();
    }

    @Override
    public List<Jot> byIds(long[] ids) {
        return new QueriedJots(new JotsByIds(db, ids)).value();
    }
}
