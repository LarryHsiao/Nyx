package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to mark a Tag as jot's tag.
 */
public class NewJotTagById implements Action {
    private final Source<Connection> conSource;
    private final long id;
    private final long jotId;
    private final long tagId;
    private final int version;
    private final int delete;

    public NewJotTagById(
        Source<Connection> conSource,
        long id, long jotId,
        long tagId,
        int version,
        int delete
    ) {
        this.conSource = conSource;
        this.id = id;
        this.jotId = jotId;
        this.tagId = tagId;
        this.version = version;
        this.delete = delete;
    }

    @Override
    public void fire() {
        Connection conn = conSource.value();
        try (PreparedStatement stmt = conn.prepareStatement(
            // language=H2
            "INSERT INTO TAG_JOT(jot_id, tag_id, version, delete) VALUES ( ?,?,?,? )"
        )) {
            stmt.setLong(1, jotId);
            stmt.setLong(2, tagId);
            stmt.setInt(3, version);
            stmt.setInt(4, delete);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
