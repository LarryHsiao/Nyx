package com.larryhsiao.nyx.core.sync;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JotIndexJsonTest {

    /**
     * Check the json serialization/deserialization result
     */
    @Test
    void toJsonAndBack() {
        assertEquals(
            "{\"id\":1,\"version\":1,\"deleted\":true,\"tagIds\":[1]}",
            new JotIndexJson(
                new JsonJotIndex(
                    new JotIndexJson(
                        new ConstJotIndex(
                            1,
                            1,
                            true,
                            Collections.singletonList(1L)
                        )
                    ).value()
                )
            ).value().toString()
        );
    }
}