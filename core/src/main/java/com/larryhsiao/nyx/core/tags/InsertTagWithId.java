package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class InsertTagWithId implements Action {
    private final Source<Connection> db;
    private final Tag tag;

    public InsertTagWithId(Source<Connection> db, Tag tag) {
        this.db = db;
        this.tag = tag;
    }

    @Override
    public void fire() {
        try (final PreparedStatement stmt = db.value().prepareStatement(
            "INSERT INTO TAGS(ID, TITLE, VERSION, DELETE) VALUES ( ?, ?, ?, ?);"
        )) {
            stmt.setLong(1, tag.id());
            stmt.setString(2, tag.title());
            stmt.setInt(3, tag.version());
            stmt.setInt(4, tag.deleted() ? 1 : 0);
            stmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
