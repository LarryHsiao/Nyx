package com.larryhsiao.nyx.core.metadata

import com.silverhetch.clotho.Source
import com.silverhetch.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Connection

/**
 * Test for [QueriedMetadata].
 */
internal class QueriedMetadataTest {
    @Test
    internal fun normalCase() {
        val db: Source<Connection> = MetadataDb(MemoryH2Conn())
        CreatedMetadata(db, ConstMetadata(
            -1,
            1,
            "type",
            "title",
            "content"
        )).value()
        val inserted = QueriedMetadata(
            MetadataByJotId(db, 1)
        ).value()

        assertEquals(1, inserted.size)
        assertEquals("RAW", inserted[0].type().name)
        assertEquals("title", inserted[0].title())
        assertEquals("content", inserted[0].content())
        assertEquals(1L, inserted[0].version())
    }
}