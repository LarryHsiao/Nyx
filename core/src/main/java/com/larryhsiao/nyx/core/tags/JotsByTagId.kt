package com.larryhsiao.nyx.core.tags

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to build a query of Jots by Tag id.
 */
class JotsByTagId(
    private val connSource: Source<Connection>,
    private val tagId: Source<Long>
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = connSource.value().prepareStatement( // language=H2
                "SELECT * FROM jots " +
                    "INNER JOIN TAG_JOT ON JOTS.ID=TAG_JOT.JOT_ID " +
                    "WHERE TAG_ID=? AND JOTS.DELETE = 0;"
            )
            stmt.setLong(1, tagId.value())
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}