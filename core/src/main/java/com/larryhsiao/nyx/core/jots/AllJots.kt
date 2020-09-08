package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * All jots in db.
 */
class AllJots @JvmOverloads constructor(
    private val conn: Source<Connection>,
    private val includedDelete: Boolean = false
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            if (includedDelete) {
                conn.value().createStatement().executeQuery( // language=H2
                    "SELECT * FROM jots ORDER BY CREATEDTIME DESC;"
                )
            } else {
                conn.value().createStatement().executeQuery( // language=H2
                    "SELECT * FROM jots WHERE DELETE = 0 ORDER BY CREATEDTIME DESC;"
                )
            }
        } catch (e: SQLException) {
            throw IllegalArgumentException(e)
        }
    }
}