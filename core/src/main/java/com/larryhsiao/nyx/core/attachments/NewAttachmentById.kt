package com.larryhsiao.nyx.core.attachments

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * New Attachment of a Jot, update by an object.
 */
class NewAttachmentById(
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
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}