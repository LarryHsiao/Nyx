package com.larryhsiao.nyx.core.attachments;

import com.silverhetch.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Source to query attachments by attached Jot id.
 */
public class AllAttachments implements Source<ResultSet> {
    private final Source<Connection> dbSource;
    private final boolean includeDeleted;

    public AllAttachments(Source<Connection> dbSource) {
        this(dbSource, false);
    }

    public AllAttachments(Source<Connection> dbSource, boolean includeDeleted) {
        this.dbSource = dbSource;
        this.includeDeleted = includeDeleted;
    }

    @Override
    public ResultSet value() {
        try {
            if (includeDeleted) {
                PreparedStatement stmt = dbSource.value().prepareStatement(
                    // language=H2
                    "SELECT * FROM attachments "
                );
                return stmt.executeQuery();
            } else {
                PreparedStatement stmt = dbSource.value().prepareStatement(
                    // language=H2
                    "SELECT * FROM attachments " +
                        "WHERE DELETE = 0;"
                );
                return stmt.executeQuery();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
