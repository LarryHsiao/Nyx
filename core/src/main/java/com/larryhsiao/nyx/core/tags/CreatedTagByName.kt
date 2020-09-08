package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to build a Tag that have a row in the db.
 */
public class CreatedTagByName implements Source<Tag> {
    private final Source<Connection> db;
    private final String title;

    public CreatedTagByName(Source<Connection> db, String title) {
        this.db = db;
        this.title = title;
    }

    @Override
    public Tag value() {
        try {
            Connection con = db.value();
            PreparedStatement stmt = con.prepareStatement(
                // language=H2
                "SELECT * FROM TAGS WHERE TITLE=?1"
            );
            stmt.setString(1, title);
            final ResultSet res = stmt.executeQuery();
            if (res.next()) {
                return new ConstTag(
                    res.getLong("id"),
                    res.getString("title"),
                    res.getInt("version"),
                    res.getInt("delete") == 1
                );
            }
            res.close();
            stmt.close();
            return new NewTag(db, title).value();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
