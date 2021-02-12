package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to update a {@link JotTag}
 */
public class UpdatedJotTag implements Action {
    private final Source<Connection> db;
    private final JotTag newJotTag;

    public UpdatedJotTag(Source<Connection> db, JotTag newJotTag) {
        this.db = db;
        this.newJotTag = newJotTag;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = db.value().prepareStatement( // language=H2
            "UPDATE TAG_JOT " +
                "SET VERSION = VERSION + 1, DELETE=?3 " +
                "WHERE JOT_ID=?1 AND TAG_ID=?2;"
        )) {
            stmt.setLong(1, newJotTag.jotId());
            stmt.setLong(2, newJotTag.tagId());
            stmt.setLong(3, newJotTag.deleted() ? 1 : 0);
            stmt.execute();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
