package com.larryhsiao.nyx.core.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory {@link JotTags}.
 */
public class MemoryJotTags implements JotTags {
    private final Map<String, JotTag> jotTags = new HashMap<>();

    @Override
    public List<JotTag> all() {
        return new ArrayList<>(jotTags.values());
    }

    @Override
    public void link(long jotId, long tagId) {
        for (JotTag jotTag : jotTags.values()) {
            if (jotTag.jotId() == jotId && jotTag.tagId() == tagId) {
                jotTags.put(
                    jotId + " " + tagId,
                    new WrappedJotTags(jotTag) {
                        @Override
                        public boolean deleted() {
                            return false;
                        }

                        @Override
                        public int version() {
                            return super.version() + 1;
                        }
                    });
                return;
            }
        }
        jotTags.put(
            jotId + " " + tagId,
            new ConstJotTag(
                jotId, tagId, false, 1
            )
        );
    }

    @Override
    public void update(JotTag newJotTag) {
        for (JotTag jotTag : jotTags.values()) {
            if (jotTag.jotId() == newJotTag.jotId() && jotTag.tagId() == newJotTag.tagId()) {
                jotTags.put(
                    newJotTag.jotId() + " " + newJotTag.tagId(),
                    new WrappedJotTags(newJotTag) {
                        @Override
                        public int version() {
                            return jotTag.version() + 1;
                        }
                    });
                return;
            }
        }
        throw new IllegalArgumentException(
            "Link not exist, jot id: " + newJotTag.jotId() +
                " tag id: " + newJotTag.tagId()
        );
    }

    @Override
    public void deleteByIds(long jotId, long tagId) {
        jotTags.remove(jotId + " " + tagId);
    }
}
