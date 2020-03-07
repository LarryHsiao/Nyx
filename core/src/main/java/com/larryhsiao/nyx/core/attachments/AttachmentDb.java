package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Source;

import java.sql.Connection;

/**
 * Source to Build Attachment db connection
 */
public class AttachmentDb implements Source<Connection> {
    private final Source<Connection> source;

    public AttachmentDb(Source<Connection> source) {
        this.source = source;
    }

    @Override
    public Connection value() {
        try {
            Connection conn = source.value();
            conn.createStatement().execute(
                // language=H2
                "CREATE TABLE IF NOT EXISTS attachments(" +
                    "id integer not null auto_increment, " +
                    "uri text not null, " +
                    "jot_id integer not null ," +
                    "version integer not null default 1, " +
                    "delete integer not null default 0" +
                    ");"
            );
            return conn;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
