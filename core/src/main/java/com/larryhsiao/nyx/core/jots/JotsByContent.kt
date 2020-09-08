package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to query jots by given ids.
 *
 * @todo #183 Consider to remove this
 */
class JotsByContent(
    private val dbSource: Source<Connection>,
    private val keyword: String
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = dbSource.value().prepareStatement( // language=H2
                "SELECT * FROM jots " +
                    "WHERE UPPER(CONTENT) like UPPER(?) " +
                    "AND DELETE = 0 " +
                    "ORDER BY CREATEDTIME DESC;"
            )
            stmt.setString(1, "%$keyword%")
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}