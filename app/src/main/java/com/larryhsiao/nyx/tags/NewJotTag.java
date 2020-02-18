package com.larryhsiao.nyx.tags;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
        Connection conn = conSource.value();
        try (PreparedStatement stmt = conn.prepareStatement(
            // language=H2
            "INSERT INTO TAG_JOT(jot_id, tag_id) VALUES ( ?,? )"
        )) {
            stmt.setLong(1, jotId.value());
            stmt.setLong(2, tagId.value());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
