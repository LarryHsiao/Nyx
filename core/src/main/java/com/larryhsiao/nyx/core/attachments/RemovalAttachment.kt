package com.larryhsiao.nyx.core.attachments

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Remove Attachments by given Jot id.
 */
class RemovalAttachment(private val dbConn: Source<Connection>, private val id: Long) : Action {
    override fun fire() {
        try {
            dbConn.value().prepareStatement( // language=H2
                "UPDATE attachments " +
                    "SET DELETE = 1 , VERSION = VERSION + 1 " +
                    "WHERE ID=?1;"
            ).use { stmt ->
                stmt.setLong(1, id)
                stmt.executeUpdate()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}