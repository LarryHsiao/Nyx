package com.larryhsiao.nyx.core.attachments;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonAttachmentTest {
    @Test
    void toJsonAndBack() {
        assertEquals(
            "{\"id\":1,\"jotId\":1,\"uri\":\"uri\",\"version\":1,\"deleted\":true}",
            new AttachmentJson(
                new JsonAttachment(
                    new AttachmentJson(
                        new ConstAttachment(
                            1,
                            1,
                            "uri",
                            1,
                           1
                        )
                    ).value()
                )
            ).value().toString()
        );
    }
}