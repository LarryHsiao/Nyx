package com.larryhsiao.nyx.core.jots

import com.larryhsiao.nyx.core.jots.filter.Filter
import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet

/**
 * Source to build a [ResultSet].
 * Note: The filter should have all field filled up to prevent unexpected result.
 * In that case, use [JotsByCheckedFilter].
 */
class JotsByFilter(
    private val dbSource: Source<Connection>,
    private val filter: Filter
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = dbSource.value().prepareStatement( // language=H2
                "SELECT JOTS.* FROM jots " +
                    "LEFT JOIN TAG_JOT ON TAG_JOT.JOT_ID = JOTS.ID " +
                    "LEFT JOIN TAGS ON TAG_JOT.TAG_ID = TAGS.ID " +
                    "WHERE CAST(JOTS.CREATEDTIME AS DATE) >= ? " +
                    "AND CAST(JOTS.CREATEDTIME AS DATE) <= ? " +
                    "AND JOTS.DELETE = 0 " +
                    "AND (UPPER(JOTS.content) like UPPER(?) OR UPPER(TAGS.TITLE) like UPPER(?))" +
                    "GROUP BY JOTS.ID, JOTS.CREATEDTIME " +
                    "ORDER BY JOTS.CREATEDTIME DESC;"
            )
            val started = Date(filter.dateRange()!![0])
            val ended = Date(filter.dateRange()!![1])
            stmt.setDate(1, started)
            stmt.setDate(2, ended)
            stmt.setString(3, "%" + filter.keyword() + "%")
            stmt.setString(4, "%" + filter.keyword() + "%")
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}