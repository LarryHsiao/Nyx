package com.larryhsiao.nyx.core.metadata

import com.silverhetch.clotho.Source
import java.sql.Connection
import java.sql.SQLException

/**
 * Source to build updated metadata.
 */
class UpdatedMetadata(
    private val db: Source<Connection>,
    private val metadata: Metadata,
    private val increaseVer: Boolean = true
) : Source<Metadata> {
    override fun value(): Metadata {
        db.value().prepareStatement(
            // language=H2
            """//
UPDATE METADATA
SET TITLE=?,
    COMMENT=?,
    VALUE_DECIMAL=?,
    VALUE=?,
    DELETED=?,
    VERSION=?
WHERE JOT_ID = ?"""
        ).use { stmt ->
            stmt.setString(1, metadata.title())
            stmt.setString(2, metadata.comment())
            stmt.setBigDecimal(3, metadata.valueBigDecimal())
            stmt.setString(4, metadata.value())
            stmt.setInt(5, if (metadata.deleted()) 1 else 0)
            stmt.setLong(6, if (increaseVer) metadata.version() + 1 else metadata.version())
            stmt.setLong(7, metadata.jotId())
            val result = stmt.executeUpdate()
            if (result == 1) {
                return object : WrappedMetadata(metadata) {
                    override fun version(): Long {
                        return super.version() + 1
                    }
                }
            } else {
                throw SQLException("Update failed: " + metadata.id())
            }
        }
    }
}