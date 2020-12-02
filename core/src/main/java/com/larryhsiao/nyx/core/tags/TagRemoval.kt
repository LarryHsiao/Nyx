package com.larryhsiao.nyx.core.tags

import com.larryhsiao.clotho.Action
import com.larryhsiao.clotho.Source
import java.sql.Connection

/**
 * Action to remove
 */
class TagRemoval(private val db: Source<Connection>, private val id: Long) : Action {
    override fun fire() {
        try {
            val linkRemoval = db.value().prepareStatement( // language=H2
                "UPDATE TAG_JOT " +
                    "SET DELETE = 1, VERSION = VERSION + 1 " +
                    "WHERE TAG_ID=?;"
            )
            linkRemoval.setLong(1, id)
            linkRemoval.executeUpdate()
            linkRemoval.close()
            val tagRemoval = db.value().prepareStatement( // language=H2
                "UPDATE tags " +
                    "SET DELETE = 1, VERSION = VERSION + 1 " +
                    "WHERE id=?;"
            )
            tagRemoval.setLong(1, id)
            tagRemoval.executeUpdate()
            tagRemoval.close()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}