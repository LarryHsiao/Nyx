package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Source;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Source to build a {@link ResultSet} from
 * querying all the {@link JotTag}.
 */
public class AllJotTags implements Source<ResultSet> {
    private final Source<Connection> db;

    public AllJotTags(Source<Connection> db) {
        this.db = db;
    }

    @Override
    public ResultSet value() {
        try {
            return db.value().createStatement().executeQuery(
                // language=H2
                "SELECT * FROM TAG_JOT;"
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
