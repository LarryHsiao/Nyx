package com.larryhsiao.nyx.core.sync.server;

import com.larryhsiao.nyx.core.jots.ConstJot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * Test for {@link JotsJsonArray}.
 */
class JotsJsonArrayTest {
    @Test
    void normalCase() {
        Assertions.assertEquals(
            "[{\"id\":1,"
                + "\"title\":\"\","
                + "\"content\":\"content\","
                + "\"deleted\":false,"
                + "\"createdTime\":0,"
                + "\"location\":[0.0,0.0],"
                + "\"mood\":\"mood\","
                + "\"privateLock\":false,"
                + "\"version\":1}]",
            new JotsJsonArray(
                Collections.singletonList(new ConstJot(
                    1L,
                    "",
                    "content",
                    0L,
                    new double[]{0.0, 0.0},
                    "mood",
                    1,
                    false,
                    false
                ))
            ).value().toString()
        );
    }
}