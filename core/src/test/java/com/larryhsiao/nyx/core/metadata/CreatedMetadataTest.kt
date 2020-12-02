package com.larryhsiao.nyx.core.metadata

import com.larryhsiao.nyx.core.NyxDb
import com.larryhsiao.nyx.core.jots.AllJots
import com.larryhsiao.nyx.core.jots.JotsDb
import com.larryhsiao.nyx.core.jots.NewJot
import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.Connection

/**
 * Test for [CreatedMetadata].
 */
internal class CreatedMetadataTest {
    /**
     * Check if insert success.
     */
    @Test
    internal fun normalCase() {
        val db: Source<Connection> = MetadataDb(MemoryH2Conn())
        CreatedMetadata(db, ConstMetadata(
            -1,
            1,
            Metadata.Type.TEXT,
            "title",
            "content"
        )).value()
        MetadataByJotId(db, 1).value().use { res ->
            var count = 0
            while (res.next()) {
                count++
            }
            Assertions.assertEquals(1, count)
        }
    }
}