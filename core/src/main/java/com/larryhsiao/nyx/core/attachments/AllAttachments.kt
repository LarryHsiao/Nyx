package com.larryhsiao.nyx.core.attachments

import com.larryhsiao.clotho.Source
import java.sql.Connection
import java.sql.ResultSet

/**
 * Source to query attachments by attached Jot id.
 */
class AllAttachments @JvmOverloads constructor(
    private val dbSource: Source<Connection>,
    private val includeDeleted: Boolean = false
) : Source<ResultSet> {
    override fun value(): ResultSet {
        return try {
            if (includeDeleted) {
                val stmt = dbSource.value().prepareStatement( // language=H2
                    "SELECT * FROM attachments "
                )
                stmt.executeQuery()
            } else {
                val stmt = dbSource.value().prepareStatement( // language=H2
                    "SELECT * FROM attachments " +
                        "WHERE DELETE = 0;"
                )
                stmt.executeQuery()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}