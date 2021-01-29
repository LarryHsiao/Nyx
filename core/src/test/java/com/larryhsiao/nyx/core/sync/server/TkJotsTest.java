package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.clotho.database.h2.MemoryH2Conn;
import com.larryhsiao.nyx.core.MemoryNyx;
import com.larryhsiao.nyx.core.jots.ConstJot;
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
        new NewJot(db, new ConstJot(
            1, "title","content", 1L
        )).value();
        Assertions.assertEquals(
            "[{\"id\":1,\"title\":\"title\","
                + "\"content\":\"content\","
                + "\"deleted\":false,\"createdTime\":1,"
                + "\"location\":[0.0,0.0],\"mood\":\"\","
                + "\"privateLock\":false,\"version\":1}]",
            new RsPrint(
                new TkJots(new MemoryNyx()).act(new RqFake("GET"))
            ).printBody()
        );
    }
}