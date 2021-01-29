package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.database.h2.MemoryH2Conn;
import com.larryhsiao.nyx.core.LocalNyx;
import com.larryhsiao.nyx.core.MemoryNyx;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.AttachmentDb;
import com.larryhsiao.nyx.core.attachments.ConstAttachment;
import com.larryhsiao.nyx.core.attachments.NewAttachment;
import com.larryhsiao.nyx.core.attachments.file.MemoryNyxFiles;
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
        Nyx nyx = new MemoryNyx();
        nyx.attachments().newAttachment(
            new ConstAttachment(
                1, 1, "uri",1, 0
            )
        );
        Assertions.assertEquals(
            "[{\"id\":1,\"title\":\"uri\"}]",
            new RsPrint(
                new TkAttachments(nyx).act(new RqFake("GET"))
            ).printBody()
        );
    }
}