package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Db connection source with initial sql.
 */
class JotsDb(private val connSource: Source<Connection>) : Source<Connection> {
    override fun value(): Connection {
        return try {
            val conn = connSource.value()
            conn.createStatement().executeUpdate(
                // language=H2
                """CREATE TABLE IF NOT EXISTS jots
(
    id          integer                  not null auto_increment,
    title       text                     not null default '',
    content     text                     not null,
    createdTime timestamp with time zone not null,
    location    geometry,
    mood        varchar                  not null default '',
    version     integer                  not null default 1,
    delete      integer                  not null default 0,
    private     boolean                  not null default false
);""")
            conn
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}