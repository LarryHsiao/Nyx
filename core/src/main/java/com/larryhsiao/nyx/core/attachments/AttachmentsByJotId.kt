package com.larryhsiao.nyx.core.attachments

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to query attachments by attached Jot id.
 */
class AttachmentsByJotId @JvmOverloads constructor(
    private val dbSource: Source<Connection>,
    private val jotId: Long,
    private val includeDeleted: Boolean = false
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            if (includeDeleted) {
                val stmt = dbSource.value().prepareStatement( // language=H2
                    "SELECT * FROM attachments " +
                        "WHERE jot_id = ?"
                )
                stmt.setLong(1, jotId)
                stmt.executeQuery()
            } else {
                val stmt = dbSource.value().prepareStatement( // language=H2
                    "SELECT * FROM attachments " +
                        "WHERE jot_id = ? " +
                        "AND DELETE = 0;"
                )
                stmt.setLong(1, jotId)
                stmt.executeQuery()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}