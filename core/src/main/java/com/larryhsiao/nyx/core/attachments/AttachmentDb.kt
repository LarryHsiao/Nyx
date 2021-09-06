package com.larryhsiao.nyx.core.attachments

import com.larryhsiao.clotho.Source
import java.sql.Connection

/**
 * Source to Build Attachment db connection
 */
class AttachmentDb(private val source: Source<Connection>) : Source<Connection> {
    override fun value(): Connection {
        return try {
            val conn = source.value()
            conn.createStatement().execute( // language=H2
                "CREATE TABLE IF NOT EXISTS attachments(" +
                    "id integer not null auto_increment primary key, " +
                    "uri text not null, " +
                    "jot_id integer not null ," +
                    "version integer not null default 1, " +
                    "delete integer not null default 0" +
                    ");"
            )
            conn
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}