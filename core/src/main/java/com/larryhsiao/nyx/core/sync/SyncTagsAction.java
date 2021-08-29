package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SyncTagsAction implements Action {
    private final Tags localTags;
    private final NyxIndexes remoteIndexes;

    public SyncTagsAction(Tags localTags, NyxIndexes remoteIndexes) {
        this.localTags = localTags;
        this.remoteIndexes = remoteIndexes;
    }

    @Override
    public void fire() {
        final List<Tag> remoteUpdates = new ArrayList<>();
        final Map<Long, Tag> localTagMap = localTags
            .all()
            .stream()
            .collect(Collectors.toMap(Tag::id, Function.identity()));
        final Map<Long, Tag> remoteTags = remoteIndexes.tags()
            .stream()
            .collect(Collectors.toMap(Tag::id, Function.identity()));
        for (Tag remoteTag : remoteTags.values()) {
            if (localTagMap.containsKey(remoteTag.id())) {
                final Tag localTag = localTagMap.get(remoteTag.id());
                if (remoteTag.version() > localTag.version()) {
                    localTags.replace(remoteTag);
                } else if (remoteTag.version() < localTag.version()) {
                    remoteUpdates.add(localTag);
                }
                // Remove updated tag so new tag at local remains.
                localTagMap.remove(remoteTag.id());
            } else {
                localTags.create(remoteTag);
            }
        }
        remoteUpdates.addAll(localTagMap.values());
        remoteIndexes.updateTags(remoteUpdates);
    }
}
