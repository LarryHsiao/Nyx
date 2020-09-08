package com.larryhsiao.nyx.core.tags

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Source to build tags by Jot id.
 */
class TagsByJotId @JvmOverloads constructor(
    private val connSource: Source<Connection>,
    private val jotId: Long,
    private val includingDeleted: Boolean = false
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt: PreparedStatement
            stmt = if (includingDeleted) {
                connSource.value().prepareStatement( //language=H2
                    "SELECT  * FROM TAGS " +
                        "INNER JOIN TAG_JOT TJ on TAGS.ID = TJ.TAG_ID " +
                        "WHERE TJ.JOT_ID=?;"
                )
            } else {
                connSource.value().prepareStatement( //language=H2
                    "SELECT  * FROM TAGS " +
                        "INNER JOIN TAG_JOT TJ on TAGS.ID = TJ.TAG_ID " +
                        "WHERE TJ.JOT_ID=? AND TJ.DELETE = 0 AND TAGS.DELETE=0;"
                )
            }
            stmt.setLong(1, jotId)
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}