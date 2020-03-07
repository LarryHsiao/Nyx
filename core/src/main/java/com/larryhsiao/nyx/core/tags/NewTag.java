package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * Source to build a Tag that just created.
 */
public class NewTag implements Source<Tag> {
    private final Source<Connection> connSource;
    private final Tag tag;

    public NewTag(Source<Connection> connSource, String title) {
        this(connSource, new ConstTag(-1L, title, 1));
    }

    public NewTag(Source<Connection> db, Tag tag) {
        this.connSource = db;
        this.tag = tag;
    }

    @Override
    public Tag value() {
        try (PreparedStatement stmt = connSource.value().prepareStatement(
            // language=H2
            "INSERT INTO TAGS (TITLE)VALUES ( ? );"
            , RETURN_GENERATED_KEYS
        )) {
            stmt.setString(1, tag.title());
            stmt.executeUpdate();
            ResultSet res = stmt.getGeneratedKeys();
            if (!res.next()) {
                throw new IllegalArgumentException("Creating tag failed, title: " + tag.title());
            }
            long newId = res.getLong(1);
            return new WrappedTag(tag) {
                @Override
                public long id() {
                    return newId;
                }
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
