package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to query attachments by attached Jot id.
 */
public class ByUrl implements Source<ResultSet> {
    private final Source<Connection> dbSource;
    private final String uri;

    public ByUrl(Source<Connection> dbSource, String uri) {
        this.dbSource = dbSource;
        this.uri = uri;
    }

    @Override
    public ResultSet value() {
        try {
            PreparedStatement stmt = dbSource.value().prepareStatement(
                // language=H2
                "SELECT * FROM attachments "
                    + "WHERE URI=?"
            );
            stmt.setString(1, uri);
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
