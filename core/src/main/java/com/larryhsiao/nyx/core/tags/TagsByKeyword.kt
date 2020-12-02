package com.larryhsiao.nyx.core.tags

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to build tags by Jot id.
 */
class TagsByKeyword(
    private val connSource: Source<Connection>,
    private val keyword: String
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = connSource.value().prepareStatement( //language=H2
                "SELECT  * FROM TAGS " +
                    "WHERE UPPER(TITLE) LIKE UPPER(?) AND DELETE = 0;"
            )
            stmt.setString(1, "%$keyword%")
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}