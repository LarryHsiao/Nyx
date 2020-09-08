package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * All jots in db.
 */
class AllTags @JvmOverloads constructor(
    private val conn: Source<Connection>,
    private val icnludeDeleted: Boolean = false
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            if (icnludeDeleted) {
                conn.value().createStatement().executeQuery( // language=H2
                    "SELECT * FROM TAGS;"
                )
            } else {
                conn.value().createStatement().executeQuery( // language=H2
                    "SELECT * FROM TAGS WHERE DELETE = 0;"
                )
            }
        } catch (e: SQLException) {
            throw IllegalArgumentException(e)
        }
    }
}