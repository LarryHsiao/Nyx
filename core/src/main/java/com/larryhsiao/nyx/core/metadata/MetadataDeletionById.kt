package com.larryhsiao.nyx.core.metadata

import com.silverhetch.clotho.Action
import com.silverhetch.clotho.Source
import java.sql.Connection

/**
 * Action to delete metadata by given metadata id.
 */
class MetadataDeletionById(
    private val db: Source<Connection>,
    private val id: Long
) : Action {
    override fun fire() {
        db.value().prepareStatement(
            // language=H2
            """//
UPDATE METADATA
SET DELETED=1,
    VERSION = VERSION + 1
WHERE ID = ?"""
        ).use {
            it.setLong(1, id)
            it.executeUpdate()
        }
    }
}