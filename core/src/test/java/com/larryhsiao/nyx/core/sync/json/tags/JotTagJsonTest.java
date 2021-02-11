package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.nyx.core.tags.ConstJotTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link JotTagJson}.
 */
class JotTagJsonTest {

    /**
     * Checkout output string.
     */
    @Test
    void normalCase() {
        assertEquals(
            "{\"jot_id\":1,\"tag_id\":1,\"version\":1,\"deleted\":true}",
            new JotTagJson(
                new ConstJotTag(
                    1,
                    1,
                    true,
                    1
                )
            ).value().toString()
        );
    }
}