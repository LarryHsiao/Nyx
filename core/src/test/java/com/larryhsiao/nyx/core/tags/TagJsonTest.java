package com.larryhsiao.nyx.core.tags;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.json.Json;

import java.io.StringReader;

/**
 * Test for {@link TagJson}
 */
class TagJsonTest {
    /**
     * Check if the {@link TagJson} can be convert back to {@link Tag}
     */
    @Test
    void checkToJsonAndBack() {
        Assertions.assertEquals(
            "{\"id\":1,\"title\":\"title\",\"version\":1,\"deleted\":true}",
            new TagJson(
                new JsonTag(
                    new TagJson(
                        new ConstTag(
                            1,
                            "title",
                            1,
                            true
                        )
                    ).value()
                )
            ).value().toString()
        );
    }
}