package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.util.List;

/**
 * Local database implementation of {@link Tags}.
 *
 * @todo #112-1 Local implementation of {@link Tags}.
 */
public class LocalTags implements Tags {
    private final Source<Connection> db;

    public LocalTags(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public List<Tag> all() {
        return new QueriedTags(new AllTags(db, true)).value();
    }

    @Override
    public List<Tag> byJotId(long id) { return new QueriedTags(new TagsByJotId(db, id)).value();
    }

    @Override
    public Tag create(String name) {
        return new CreatedTagByName(db, name).value();
    }

    @Override
    public Tag create(Tag tag) {
        return new NewTag(db, tag).value();
    }

    @Override
    public void update(Tag tag) {
        new UpdateTag(db, tag).fire();
    }

    @Override
    public void deleteById(long id) {
        new TagRemoval(db, id).fire();
    }
}
