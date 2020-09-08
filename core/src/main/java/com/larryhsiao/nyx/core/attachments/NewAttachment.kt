package com.larryhsiao.nyx.core.attachments

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.Statement

/**
 * New Attachment of a Jot
 */
class NewAttachment(
    private val source: Source<Connection>,
    private val uri: String,
    private val jotId: Long
) : Source<Attachment?> {
    override fun value(): Attachment {
        try {
            source.value().prepareStatement( // language=H2
                "INSERT INTO attachments(uri, jot_id) " +
                    "VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { stmt ->
                stmt.setString(1, uri)
                stmt.setLong(2, jotId)
                stmt.executeUpdate()
                val res = stmt.generatedKeys
                require(res.next()) { "Creating Attachment failed, jotId: $jotId, Uri: $uri" }
                return ConstAttachment(res.getLong(1),
                    jotId,
                    uri,
                    1,
                    0
                )
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}