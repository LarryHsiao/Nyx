package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

/**
 * Source to build jot by given date.
 */
class JotsByTimestampRange(
    private val db: Source<Connection>,
    private val started: Long,
    private val ended: Long
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            // language=H2
            val stmt = db.value().prepareStatement(
                """
SELECT *
FROM jots
WHERE CREATEDTIME BETWEEN ? AND ?
AND DELETE = 0;""")
            stmt.setTimestamp(1, Timestamp(started))
            stmt.setTimestamp(2, Timestamp(ended))
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}