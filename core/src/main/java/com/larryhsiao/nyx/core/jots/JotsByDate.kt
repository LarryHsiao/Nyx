package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet

/**
 * Source to build jot by given date.
 */
class JotsByDate(private val date: Date, private val db: Source<Connection>) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = db.value().prepareStatement( // language=H2
                "SELECT * FROM jots " +
                    "WHERE CAST(CREATEDTIME AS DATE) = ? " +
                    "AND DELETE = 0;"
            )
            stmt.setDate(1, date)
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}