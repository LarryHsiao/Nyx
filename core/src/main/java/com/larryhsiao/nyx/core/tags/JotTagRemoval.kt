package com.larryhsiao.nyx.core.tags

import com.larryhsiao.clotho.Action
import com.larryhsiao.clotho.Source
import java.sql.Connection

/**
 * Action to remove attached a tag from jot.
 */
class JotTagRemoval(
    private val conn: Source<Connection>,
    private val jotId: Long,
    private val tagId: Long
) : Action {
    override fun fire() {
        try {
            conn.value().prepareStatement( // language=H2
                "UPDATE tag_jot " +
                    "SET DELETE = 1,  VERSION = VERSION + 1" +
                    " WHERE jot_id=? AND TAG_ID=?"
            ).use { stmt ->
                stmt.setLong(1, jotId)
                stmt.setLong(2, tagId)
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}