package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Source;

import java.sql.Connection;

/**
 * Local database implementation of {@link Tags}.
 *
 * @todo #112-1 Local implementation of {@link Tags}.
 */
public class LocalTags implements Tags {
    private final Source<Connection> db;

    public LocalTags(Source<Connection> db) {
        this.db = db;
    }
}
