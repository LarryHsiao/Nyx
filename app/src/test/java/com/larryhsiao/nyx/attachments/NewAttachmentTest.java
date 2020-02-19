package com.larryhsiao.nyx.attachments;

import com.larryhsiao.nyx.jots.JotsDb;
import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.database.h2.MemoryH2Conn;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;

/**
 * Test for {@link NewAttachment}
 */
public class NewAttachmentTest {
    /**
     * Check new attachment available.
     */
    @Test
    public void checkInsert() {
        final Source<Connection> db = new AttachmentDb(new JotsDb(new MemoryH2Conn()));
        Assert.assertEquals(
            1L,
            new NewAttachment(
                db, "Uri", 1L
            ).value().id()
        );
    }
}