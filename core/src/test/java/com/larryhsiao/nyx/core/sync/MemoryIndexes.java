package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemoryIndexes implements NyxIndexes {
    private final Map<Long, JotIndex> jots;
    private final Map<Long, Tag> tags;

    public MemoryIndexes(List<JotIndex> jots, List<Tag> tags) {
        this.jots = jots.stream().collect(Collectors.toMap(JotIndex::id, Function.identity()));
        this.tags = tags.stream().collect(Collectors.toMap(Tag::id, Function.identity()));
    }

    @Override
    public List<JotIndex> jots() {
        return new ArrayList<>(jots.values());
    }

    @Override
    public List<Tag> tags() {
        return new ArrayList<>(tags.values());
    }

    @Override
    public void updateTags(List<Tag> newTags) {
        tags.putAll(newTags.stream().collect(Collectors.toMap(Tag::id, Function.identity())));
    }
}
