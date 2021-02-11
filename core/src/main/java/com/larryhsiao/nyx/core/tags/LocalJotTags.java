package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.source.ConstSource;

import java.sql.Connection;
import java.util.List;

/**
 * Object for accessing the {@link JotTags} at local database.
 */
public class LocalJotTags implements JotTags {
    private final Source<Connection> db;

    public LocalJotTags(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public List<JotTag> all() {
        return new QueriedJotTags(
            new AllJotTags(db)
        ).value();
    }

    @Override
    public void link(long jotId, long tagId) {
        new NewJotTag(
            db,
            new ConstSource<>(jotId),
            new ConstSource<>(tagId)
        ).fire();
    }

    @Override
    public void update(JotTag jotTag) {
        new UpdatedJotTag(db, jotTag).fire();
    }

    @Override
    public void deleteByIds(long jotId, long tagId) {
        new JotTagRemoval(db, jotId, tagId).fire();
    }
}
