package com.larryhsiao.nyx.core.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory {@link Tags}.
 *
 * @todo #112-2 Memory implementation of {@link Tags} .
 */
public class MemoryTags implements Tags {
    private final Map<Long, Tag> tags = new HashMap<>();

    @Override
    public List<Tag> all() {
        return new ArrayList<>(tags.values());
    }

    @Override
    public Tag newTag(Tag tag) {
        tags.put(tag.id(), tag);
        return tag;
    }

    @Override
    public void update(Tag tag) {
        tags.put(tag.id(), tag);
    }

    @Override
    public void deleteById(long id) {
        tags.remove(id);
    }
}
