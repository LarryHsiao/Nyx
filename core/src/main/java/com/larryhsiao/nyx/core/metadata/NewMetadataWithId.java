package com.larryhsiao.nyx.core.metadata;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewMetadataWithId implements Action {
    private final Source<Connection> db;
    private final Metadata metadata;

    public NewMetadataWithId(Source<Connection> db, Metadata metadata) {
        this.db = db;
        this.metadata = metadata;
    }

    @Override
    public void fire() {
        try (final PreparedStatement stmt = db.value().prepareStatement(
            // language=H2
            "INSERT INTO METADATA(ID, TYPE, TITLE, VALUE, VALUE_DECIMAL, COMMENT, JOT_ID, VERSION, DELETED)\n" +
                "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9);"
        )) {
            stmt.setLong(1, metadata.id());
            stmt.setString(2, metadata.type().name());
            stmt.setString(3, metadata.title());
            stmt.setString(4, metadata.value());
            stmt.setBigDecimal(5, metadata.valueBigDecimal());
            stmt.setString(6, metadata.comment());
            stmt.setLong(7, metadata.jotId());
            stmt.setLong(8, metadata.version());
            stmt.setInt(9, metadata.deleted() ? 1 : 0);
            stmt.execute();
            if (stmt.getUpdateCount() == 0) {
                throw new SQLException("Insert metadata failure: " + metadata.id());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
