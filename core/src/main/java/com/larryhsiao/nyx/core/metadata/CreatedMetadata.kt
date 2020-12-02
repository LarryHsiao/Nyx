package com.larryhsiao.nyx.core.metadata

import com.larryhsiao.clotho.Source
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
INSERT INTO METADATA(TYPE, TITLE, VALUE, JOT_ID, VALUE_DECIMAL, COMMENT)
values (?, ?, ?, ?, ?, ?)""",
            RETURN_GENERATED_KEYS
        ).use {
            it.setString(1, metadata.type().name)
            it.setString(2, metadata.title())
            it.setString(3, metadata.value())
            it.setLong(4, metadata.jotId())
            it.setBigDecimal(5, metadata.valueBigDecimal())
            it.setString(6, metadata.comment())
            if (it.executeUpdate() == 0) {
                throw SQLException("Insert metadata failed")
            }
            val res = it.generatedKeys
            require(res.next()) { "Create jot failed: " + metadata.value() }
            val newId = res.getLong(1)
            return object : WrappedMetadata(metadata) {
                override fun id(): Long {
                    return newId
                }
            }
        }
    }
}