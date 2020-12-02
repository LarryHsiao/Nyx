package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to query jots by given ids.
 */
class JotsByIds(
    private val dbSource: Source<Connection>,
    private val ids: LongArray
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = dbSource.value().createStatement()
            val idStr = StringBuilder()
            for (i in ids.indices) {
                if (i > 0) {
                    idStr.append(", ")
                }
                idStr.append(ids[i])
            }
            stmt.executeQuery( // language=H2
                "SELECT * FROM jots " +
                    "WHERE ID IN (" + idStr.toString() + ") " +
                    "AND DELETE = 0 " +
                    "ORDER BY CREATEDTIME DESC;"
            )
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}