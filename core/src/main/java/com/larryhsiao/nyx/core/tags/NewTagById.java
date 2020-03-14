package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Source to build a Tag that just created.
 */
public class NewTagById implements Source<Tag> {
    private final Source<Connection> connSource;
    private final Tag tag;

    public NewTagById(Source<Connection> db, Tag tag) {
        this.connSource = db;
        this.tag = tag;
    }

    @Override
    public Tag value() {
        try (PreparedStatement stmt = connSource.value().prepareStatement(
            // language=H2
            "INSERT INTO TAGS (ID, TITLE, VERSION, DELETE)VALUES ( ?, ?, ?, ? );"
        )) {
            stmt.setLong(1, tag.id());
            stmt.setString(2, tag.title());
            stmt.setInt(3, tag.version());
            stmt.setInt(4, tag.deleted() ? 1 : 0);
            stmt.executeUpdate();
            return tag;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
