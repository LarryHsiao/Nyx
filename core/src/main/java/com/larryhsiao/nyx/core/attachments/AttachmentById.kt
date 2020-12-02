package com.larryhsiao.nyx.core.attachments

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.SQLException

/**
 * Source to build [Attachment] from given id.
 */
class AttachmentById(
    private val db: Source<Connection>,
    private val id: Long
) : Source<Attachment?> {
    override fun value(): Attachment {
        try {
            db.value().prepareStatement( // language=H2
                "SELECT * FROM attachments WHERE id=?;"
            ).use { stmt ->
                stmt.setLong(1, id)
                val res = stmt.executeQuery()
                require(res.next()) { "Jot not found, id: $id" }
                return ConstAttachment(
                    res.getLong("id"),
                    res.getLong("jot_id"),
                    res.getString("uri"),
                    res.getInt("version"),
                    res.getInt("delete")
                )
            }
        } catch (e: SQLException) {
            throw IllegalArgumentException(e)
        }
    }
}