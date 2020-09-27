package com.larryhsiao.nyx.core.metadata

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

/**
 * Source to build a [Metadata].
 */
class CreatedMetadata(
    private val db: Source<Connection>,
    private val metadata: Metadata
) : Source<Metadata> {
    override fun value(): Metadata {
        db.value().prepareStatement(
            """//
INSERT INTO METADATA(TYPE, TITLE, CONTENT, JOT_ID, VERSION)
values (?, ?, ?, ?, ?)""",
            RETURN_GENERATED_KEYS
        ).use {
            it.setString(1, metadata.type().name)
            it.setString(2, metadata.title())
            it.setString(3, metadata.content())
            it.setLong(4, metadata.jotId())
            it.setLong(5, metadata.version())
            if (it.executeUpdate() == 0) {
                throw SQLException("Insert metadata failed")
            }
            val res = it.generatedKeys
            require(res.next()) { "Create jot failed: " + metadata.content() }
            val newId = res.getLong(1)
            return object : WrappedMetadata(metadata) {
                override fun id(): Long {
                    return newId
                }
            }
        }
    }
}