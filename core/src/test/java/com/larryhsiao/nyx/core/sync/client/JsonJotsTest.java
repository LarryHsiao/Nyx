package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.nyx.core.jots.ConstJot;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.sync.server.JotsJsonArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class JsonJotsTest {
    /**
     * Test if the content will be parsed.
     */
    @Test
    void normalCase() {
        List<Jot> jots = new JsonJots(
            new JotsJsonArray(
                Collections.singletonList(new ConstJot(
                    1L,
                    "",
                    "content",
                    0L,
                    new double[]{0.0, 0.0},
                    "mood",
                    1,
                    true,
                    true
                ))
            ).value()
        ).value();
        Assertions.assertEquals(1, jots.size());
        Assertions.assertEquals(1, jots.get(0).id());
        Assertions.assertEquals("", jots.get(0).title());
        Assertions.assertEquals("content", jots.get(0).content());
        Assertions.assertEquals(0L, jots.get(0).createdTime());
        Assertions.assertEquals("mood", jots.get(0).mood());
        Assertions.assertEquals(1, jots.get(0).version());
        Assertions.assertEquals(true, jots.get(0).deleted());
        Assertions.assertEquals(true, jots.get(0).privateLock());
    }
}