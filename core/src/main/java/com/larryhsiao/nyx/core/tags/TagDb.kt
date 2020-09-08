package com.larryhsiao.nyx.core.tags;

import com.silverhetch.clotho.Source;

import java.sql.Connection;

/**
 * Source to build a conneciton for tag database.
 */
public class TagDb implements Source<Connection> {
    private final Source<Connection> connSource;

    public TagDb(Source<Connection> connSource) {
        this.connSource = connSource;
    }

    @Override
    public Connection value() {
        try {
            Connection conn = connSource.value();
            conn.createStatement().executeUpdate(
                // language=H2
                "CREATE TABLE IF NOT EXISTS tags(" +
                    "id integer not null auto_increment, " +
                    "title text not null, " +
                    "version integer not null default 1, " +
                    "delete integer not null default 0" +
                    ");"
            );
            conn.createStatement().executeUpdate(
                // language=H2
                "CREATE TABLE IF NOT EXISTS tag_jot(" +
                    "jot_id integer not null, " +
                    "tag_id integer not null, " +
                    "version integer not null default 1, " +
                    "delete integer not null default 0, " +
                    "unique (jot_id, tag_id)" +
                    ");"
            );
            return conn;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
