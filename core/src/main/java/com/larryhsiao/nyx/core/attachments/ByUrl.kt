package com.larryhsiao.nyx.core.attachments

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to query attachments by attached Jot id.
 */
class ByUrl(
    private val dbSource: Source<Connection>,
    private val uri: String
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            val stmt = dbSource.value().prepareStatement(// language=H2
                "SELECT * FROM attachments "
                    + "WHERE URI=?")
            stmt.setString(1, uri)
            stmt.executeQuery()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}