package com.larryhsiao.nyx.core.attachments

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Action to update exist attachment.
 */
class UpdateAttachment @JvmOverloads constructor(
    private val db: Source<Connection>,
    private val item: Attachment,
    private val increaseVer: Boolean = true
) : Action {
    override fun fire() {
        try {
            db.value().prepareStatement( // language=H2
                "UPDATE ATTACHMENTS " +
                    "SET JOT_ID=?1, VERSION=?2, DELETE = ?3, URI = ?4 " +
                    "WHERE ID=?5"
            ).use { stmt ->
                stmt.setLong(1, item.jotId())
                stmt.setInt(2, if (increaseVer) item.version() + 1 else item.version())
                stmt.setInt(3, if (item.deleted()) 1 else 0)
                stmt.setString(4, item.uri())
                stmt.setLong(5, item.id())
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}