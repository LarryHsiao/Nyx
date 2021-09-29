package com.larryhsiao.nyx.core.attachments

import com.larryhsiao.clotho.Action
import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.SQLException

/**
 * New Attachment of a Jot, update by an object.
 */
class NewAttachmentWithId(
    private val source: Source<Connection>,
    private val item: Attachment
) : Action {
    override fun fire() {
        try {
            source.value().prepareStatement( // language=H2
                "INSERT INTO attachments(ID, uri, jot_id, VERSION, DELETE) " +
                    "VALUES (?,?,?,?,?)").use { stmt ->
                stmt.setLong(1, item.id())
                stmt.setString(2, item.uri())
                stmt.setLong(3, item.jotId())
                stmt.setInt(4, item.version())
                stmt.setInt(5, if (item.deleted()) 1 else 0)
                stmt.executeUpdate()
                if (stmt.updateCount == 0 ){
                    throw SQLException("Insert new attachment failure, id:" + item.id());
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}