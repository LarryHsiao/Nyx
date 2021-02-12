package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.nyx.core.tags.ConstJotTag;
import com.larryhsiao.nyx.core.tags.JotTag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link JotTagsJsonArray}.
 */
class JotTagsJsonArrayTest {

    /**
     * Check the converted content is same after converted back.
     */
    @Test
    void checkContentAfterTwoWayConverting() {
        final JotTag jotTag = new JsonJotTags(
            new JotTagsJsonArray(
                Collections.singletonList(
                    new ConstJotTag(2, 3, true, 1)
                )
            ).value()
        ).value().get(0);

        assertEquals(2, jotTag.jotId());
        assertEquals(3, jotTag.tagId());
        assertTrue(jotTag.deleted());
        assertEquals(1, jotTag.version());
    }
}