package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Action
import com.larryhsiao.clotho.Source
import java.sql.Connection

/**
 * Action to remove a Jot
 */
class JotRemoval(private val db: Source<Connection>, private val id: Long) : Action {
    override fun fire() {
        try {
            val removeTag = db.value().prepareStatement( // language=H2
                "UPDATE TAG_JOT " +
                    "SET DELETE=1 , VERSION = VERSION + 1 " +
                    "WHERE JOT_ID=?"
            )
            removeTag.setLong(1, id)
            removeTag.executeUpdate()
            removeTag.close()
            val removeJot = db.value().prepareStatement( // language=H2
                "UPDATE JOTS " +
                    "SET DELETE=1, VERSION = VERSION + 1 " +
                    "WHERE ID=?"
            )
            removeJot.setLong(1, id)
            removeJot.executeUpdate()
            removeJot.close()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}