package com.larryhsiao.nyx.old.attachments

import com.larryhsiao.nyx.core.attachments.AttachmentDb
import com.larryhsiao.nyx.core.attachments.NewAttachment
import com.larryhsiao.nyx.core.jots.JotsDb
import com.larryhsiao.clotho.Source
import com.larryhsiao.clotho.database.h2.MemoryH2Conn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection

/**
 * Test for [NewAttachment]
 */
class NewAttachmentTest {
    /**
     * Check new attachment available.
     */
    @Test
    fun checkInsert() {
        val db: Source<Connection> = AttachmentDb(JotsDb(MemoryH2Conn()))
        Assertions.assertEquals(
            1L,
            NewAttachment(
                db, "Uri", 1L
            ).value().id()
        )
    }
}