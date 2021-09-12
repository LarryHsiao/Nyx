package com.larryhsiao.nyx.core.metadata;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MetadataJsonTest {
    /**
     * Check Json Serialization/Deserialization
     */
    @Test
    void toJsonAndBack() {
        assertEquals(
            "{\"id\":-1,\"type\":\"OPEN_WEATHER\",\"title\":\"title\",\"value\":\"value\",\"valueBigDecimal\":1,\"comment\":\"comment\",\"jotId\":-1,\"version\":2,\"delete\":false}",
            new MetadataJson(
                new JsonMetadata(
                    new MetadataJson(
                        new ConstMetadata(
                            -1L,
                            -1L,
                            Metadata.Type.OPEN_WEATHER,
                            "value",
                            "title",
                            BigDecimal.ONE,
                            "comment",
                            2,
                            true
                        )
                    ).value()
                )
            ).value().toString()
        );
    }
}