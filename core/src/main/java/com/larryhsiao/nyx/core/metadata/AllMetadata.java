package com.larryhsiao.nyx.core.metadata;

import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;

public class AllMetadata implements Source<ResultSet> {
    private final Source<Connection> db;
    private final Boolean includeDeleted;

    public AllMetadata(Source<Connection> db, Boolean includeDelete) {
        this.includeDeleted = includeDelete;
        this.db = db;
    }

    @Override
    public ResultSet value() {
        try {
            if (includeDeleted) {
                return db.value().createStatement().executeQuery(
                    "SELECT * FROM METADATA;"
                );
            } else {
                return db.value().createStatement().executeQuery(
                    "SELECT * FROM METADATA " +
                        "WHERE DELETED = 0"
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
