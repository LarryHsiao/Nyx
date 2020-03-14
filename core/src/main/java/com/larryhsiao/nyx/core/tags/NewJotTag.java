package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Action to mark a Tag as jot's tag.
 */
public class NewJotTag implements Action {
    private final Source<Connection> conSource;
    private final Source<Long> jotId;
    private final Source<Long> tagId;

    public NewJotTag(Source<Connection> conSource, Source<Long> jotId, Source<Long> tagId) {
        this.conSource = conSource;
        this.jotId = jotId;
        this.tagId = tagId;
    }

    @Override
    public void fire() {
        try {
            final Connection conn = conSource.value();
            PreparedStatement queryTagJot = conn.prepareStatement(
                // language=H2
                "SELECT * FROM  TAG_JOT WHERE TAG_ID=?2 AND JOT_ID=?1"
            );
            queryTagJot.setLong(1, jotId.value());
            queryTagJot.setLong(2, tagId.value());
            ResultSet tagJotRes = queryTagJot.executeQuery();
            boolean jotTagExist = tagJotRes.next();
            queryTagJot.close();
            if (jotTagExist) {
                PreparedStatement stmt = conn.prepareStatement(
                    // language=H2
                    "UPDATE TAG_JOT " +
                        "SET VERSION = VERSION + 1, DELETE=0 " +
                        "WHERE JOT_ID=?1 AND TAG_ID=?2;"
                );
                stmt.setLong(1, jotId.value());
                stmt.setLong(2, tagId.value());
                stmt.executeUpdate();
                stmt.close();
            } else {
                PreparedStatement stmt = conn.prepareStatement(
                    // language=H2
                    "INSERT INTO TAG_JOT(jot_id, tag_id) VALUES ( ?,? )"
                );
                stmt.setLong(1, jotId.value());
                stmt.setLong(2, tagId.value());
                stmt.executeUpdate();
                stmt.close();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
