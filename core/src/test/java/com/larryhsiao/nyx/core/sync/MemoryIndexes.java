package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.metadata.Metadata;
import com.larryhsiao.nyx.core.tags.Tag;
import sun.security.pkcs11.wrapper.Functions;

import java.util.ArrayList;
import java.util.Collections;
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

    @Override
    public void updateJots(List<Jot> newJots) {
        for (Jot newJot : newJots) {
            jots.put(
                newJot.id(),
                new ConstJotIndex(
                    newJot.id(),
                    newJot.version(),
                    newJot.deleted(),
                    Collections.emptyList() // Update later @todo #100 Figure out if this is correct
                )
            );
        }
    }

    @Override
    public List<Attachment> attachments() {
        return Collections.emptyList();
    }

    @Override
    public void updateAttachments(List<Attachment> newAttachments) {
    }

    @Override
    public void updateMetadata(List<Metadata> newMetadata) {

    }

    @Override
    public List<Metadata> metadata() {
        return null;
    }
}
