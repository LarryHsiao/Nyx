package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Action;
import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Action to update a Tag.
 */
public class UpdateTag implements Action {
    private final Source<Connection> db;
    private final Tag tag;
    private final boolean increaseVer;

    public UpdateTag(Source<Connection> db, Tag tag) {
        this(db, tag, true);
    }

    public UpdateTag(Source<Connection> db, Tag tag, boolean increaseVer) {
        this.db = db;
        this.tag = tag;
        this.increaseVer = increaseVer;
    }

    @Override
    public void fire() {
        Connection conn = db.value();
        try (PreparedStatement stmt = conn.prepareStatement(
            //language=H2
            "UPDATE TAGS " +
                "SET TITLE=?1, VERSION = ?4, delete=?2 " +
                "WHERE ID=?3"
        )) {
            stmt.setString(1, tag.title());
            stmt.setInt(2, tag.deleted() ? 1 : 0);
            stmt.setLong(3, tag.id());
            stmt.setInt(4, increaseVer ? tag.version() + 1 : tag.version());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
