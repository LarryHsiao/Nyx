package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.database.h2.MemoryH2Conn;
import com.larryhsiao.nyx.core.jots.JotsDb;
import com.larryhsiao.nyx.core.jots.NewJot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

import java.io.IOException;
import java.sql.Connection;

class TkJotsTest {
    /**
     * Get all jots response.
     */
    @Test
    void getAll() throws IOException {
        Source<Connection> db = new JotsDb(new JotsDb(new MemoryH2Conn()));
        new NewJot(db, "title", "content").value();
        Assertions.assertEquals(
            "[{\"id\":1,\"title\":\"title\"}]",
            new RsPrint(
                new TkJots(db).act(new RqFake("GET"))
            ).printBody()
        );
    }
}