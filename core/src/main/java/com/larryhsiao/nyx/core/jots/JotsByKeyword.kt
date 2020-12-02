package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to build [ResultSet] that search Jots by keyword.
 *
 *
 * Search scope:
 * - Jot content
 * - Tag name
 */
class JotsByKeyword(
    private val dbSource: Source<Connection>,
    private val keyword: String?
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = dbSource.value().prepareStatement( // language=H2
                "SELECT JOTS.* FROM jots " +
                    "LEFT JOIN TAG_JOT ON TAG_JOT.JOT_ID = JOTS.ID " +
                    "LEFT JOIN TAGS ON TAG_JOT.TAG_ID = TAGS.ID " +
                    "WHERE (UPPER(JOTS.content) like UPPER(?) OR UPPER(TAGS.TITLE) like UPPER(?))" +
                    "AND JOTS.DELETE = 0 " +
                    "GROUP BY JOTS.ID, JOTS.CREATEDTIME " +
                    "ORDER BY JOTS.CREATEDTIME DESC;"
            )
            stmt.setString(1, "%$keyword%")
            stmt.setString(2, "%$keyword%")
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}