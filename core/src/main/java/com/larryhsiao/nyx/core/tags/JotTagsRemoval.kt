package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Action to remove all attached tag from jot.
 */
class JotTagsRemoval(private val conn: Source<Connection>, private val jotId: Long) : Action {
    override fun fire() {
        try {
            conn.value().prepareStatement( // language=H2
                "UPDATE tag_jot " +
                    "SET DELETE = 1,  VERSION = VERSION + 1" +
                    " WHERE jot_id=?"
            ).use { stmt ->
                stmt.setLong(1, jotId)
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}