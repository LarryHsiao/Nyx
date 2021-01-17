package com.larryhsiao.nyx.core.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.database.h2.MemoryH2Conn;
import com.larryhsiao.nyx.core.attachments.AttachmentDb;
import com.larryhsiao.nyx.core.attachments.NewAttachment;
import com.larryhsiao.nyx.core.jots.JotsDb;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

import java.io.IOException;
import java.sql.Connection;

class TkAttachmentsTest {
    /**
     * Check if get all is valid.
     */
    @Test
    void getAll() throws IOException {
        Source<Connection> db = new AttachmentDb(new JotsDb(new MemoryH2Conn()));
        new NewAttachment(db, "uri", 1).value();
        Assertions.assertEquals(
            "[{\"id\":1,\"title\":\"uri\"}]",
            new RsPrint(
                new TkAttachments(db).act(new RqFake("GET"))
            ).printBody()
        );
    }
}