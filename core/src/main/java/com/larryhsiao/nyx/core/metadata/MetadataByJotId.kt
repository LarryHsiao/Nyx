package com.larryhsiao.nyx.core.metadata

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Query metadata by given jotId
 */
class MetadataByJotId(
    private val connSrc: Source<Connection>,
    private val jotId: Long,
    private val includeDeleted: Boolean = false
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return if (includeDeleted) {
            val stmt = connSrc.value().prepareStatement(
                // language=H2
                """SELECT * FROM METADATA WHERE JOT_ID=?;"""
            )
            stmt.setLong(1, jotId)
            stmt.executeQuery()
        } else {
            val stmt = connSrc.value().prepareStatement(
                // language=H2
                """SELECT * FROM METADATA WHERE JOT_ID=? AND DELETED = 0;"""
            )
            stmt.setLong(1, jotId)
            stmt.executeQuery()
        }
    }
}