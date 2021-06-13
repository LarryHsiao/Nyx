package com.larryhsiao.nyx.core.metadata

import com.larryhsiao.clotho.Action
import com.larryhsiao.clotho.Source
import java.sql.Connection

/**
 * Action to delete metadata by given jot id.
 */
class MetadataDeletionByJotId(
    private val db: Source<Connection>,
    private val jotId: Long
) : Action {
    override fun fire() {
        db.value().prepareStatement(
            // language=H2
            """//
UPDATE METADATA
SET DELETED=1,
    VERSION = VERSION + 1
WHERE JOT_ID = ?"""
        ).use {
            it.setLong(1, jotId)
            it.executeUpdate()
        }
    }
}