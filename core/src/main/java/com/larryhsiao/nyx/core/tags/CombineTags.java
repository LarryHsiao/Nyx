package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.source.ConstSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;

/**
 * Action to combine two of tags.
 */
public class CombineTags implements Action {
    private final Source<Connection> db;
    private final long targetId;
    private final long combinedId;

    public CombineTags(Source<Connection> db, long targetId, long combinedId) {
        this.db = db;
        this.targetId = targetId;
        this.combinedId = combinedId;
    }

    @Override
    public void fire() {
        try (PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "UPDATE tag_jot "
                + "SET TAG_ID=?1, VERSION=VERSION+1 "
                + "WHERE TAG_ID=?2 AND JOT_ID NOT IN ("+ targetJotIds()+");"
        )) {
            stmt.setLong(1, targetId);
            stmt.setLong(2, combinedId);
            stmt.executeUpdate();
            new TagRemoval(db, combinedId).fire();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String targetJotIds() {
        Collection<Jot> jots = new QueriedJots(
            new JotsByTagId(db, new ConstSource<>(targetId))
        ).value();
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (Jot jot : jots) {
            if (index > 0) {
                builder.append(", ");
            }
            builder.append(jot.id());
            index++;
        }
        return builder.toString();
    }
}
