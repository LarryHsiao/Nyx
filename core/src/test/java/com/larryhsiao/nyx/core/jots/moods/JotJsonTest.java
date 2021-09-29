package com.larryhsiao.nyx.core.jots.moods;

import com.larryhsiao.nyx.core.jots.ConstJot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JotJsonTest {

    /**
     * Check json serialization/deserialization.
     */
    @Test
    void toJsonAndBack() {
        assertEquals(
            "{\"id\":1,\"title\":\"title\",\"content\":\"content\",\"createdTime\":1,\"location\":[0.0,0.0],\"mood\":\"mood\",\"version\":1,\"privateLock\":true,\"deleted\":true}",
            new JotJson(
                new JsonJot(
                    new JotJson(
                        new ConstJot(
                            1,
                            "title",
                            "content",
                            1,
                            new double[]{1, 1},
                            "mood",
                            1,
                            true,
                            true
                        )
                    ).value()
                )
            ).value().toString()
        );
    }
}