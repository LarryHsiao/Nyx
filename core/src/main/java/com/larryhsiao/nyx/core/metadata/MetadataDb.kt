package com.larryhsiao.nyx.core.metadata

import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Source to build a db connection which have metadata tables.
 */
class MetadataDb(
    private val connSrc: Source<Connection>
) : Source<Connection> {
    override fun value(): Connection {
        val conn = connSrc.value()
        conn.createStatement().executeUpdate(
            """//--
CREATE TABLE IF NOT EXISTS metadata
(
    id      integer not null auto_increment,
    type    text    not null,
    title   text    not null default '',
    content text    not null default '',
    jot_id  integer not null,
    version integer not null default 1,
    delete  integer not null default 0
);""")
        return conn
    }
}