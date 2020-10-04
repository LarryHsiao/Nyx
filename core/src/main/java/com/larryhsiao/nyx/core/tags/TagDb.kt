package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Source to build a conneciton for tag database.
 */
class TagDb(private val connSource: Source<Connection>) : Source<Connection> {
    override fun value(): Connection {
        return try {
            val conn = connSource.value()
            conn.createStatement().executeUpdate( // language=H2
                """//---
CREATE TABLE IF NOT EXISTS tags
(
    id      integer not null auto_increment,
    title   text    not null,
    version integer not null default 1,
    delete  integer not null default 0
);"""
            )
            conn.createStatement().executeUpdate( // language=H2
                """
CREATE TABLE IF NOT EXISTS tag_jot
(
    jot_id  integer not null,
    tag_id  integer not null,
    version integer not null default 1,
    delete  integer not null default 0,
    unique (jot_id, tag_id)
);"""
            )
            conn
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}