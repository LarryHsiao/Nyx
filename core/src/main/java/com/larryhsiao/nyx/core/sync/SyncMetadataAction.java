package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.metadata.Metadata;
import com.larryhsiao.nyx.core.metadata.MetadataSet;
import com.larryhsiao.nyx.core.tags.Tag;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SyncMetadataAction implements Action {
    private static final String metadataPath = "/%s/metadata.txt";
    private final MetadataSet localMetadataSet;
    private final NyxIndexes remoteIndexes;
    private final RemoteFiles remoteFiles;

    public SyncMetadataAction(
        MetadataSet localMetadataSet,
        NyxIndexes remoteIndexes,
        RemoteFiles remoteFiles
    ) {
        this.localMetadataSet = localMetadataSet;
        this.remoteIndexes = remoteIndexes;
        this.remoteFiles = remoteFiles;
    }

    @Override
    public void fire() {
        final List<Metadata> remoteUpdates = new ArrayList<>();
        final Map<Long, Metadata> localMetadataMap = localMetadataSet.all()
            .stream()
            .collect(Collectors.toMap(Metadata::id, Function.identity()));
        final Map<Long, Metadata> remoteMetadataMap = remoteIndexes.metadata()
            .stream()
            .collect(Collectors.toMap(Metadata::id, Function.identity()));
        for (Metadata remoteMetadata : remoteMetadataMap.values()) {
            if (localMetadataMap.containsKey(remoteMetadata.id())) {
                final Metadata localMetadata = localMetadataMap.get(remoteMetadata.id());
                if (remoteMetadata.version() > localMetadata.version()) {
                    localMetadataSet.replace(remoteMetadata);
                } else if (remoteMetadata.version() < localMetadata.version()) {
                    remoteUpdates.add(localMetadata);
                }
                // Remove updated metadata so new metadata at local remains.
                localMetadataMap.remove(remoteMetadata.id());
            } else {
                localMetadataSet.createWithId(remoteMetadata);
            }
        }
        remoteUpdates.addAll(localMetadataMap.values());
        updateRemote(remoteUpdates);
    }

    private void updateRemote(List<Metadata> remoteUpdates) {
//        final Map<Long, String> updates = remoteUpdates.stream()
//            .collect(Collectors.toMap(
//                Metadata::jotId,
//                Metadata::title,
//                {it-> ""}
//            ));
//        for (Metadata remoteUpdate : remoteUpdates) {
//            remoteFiles.post(
//                String.format(metadataPath, remoteUpdate.id() + ""),
//                new ByteArrayInputStream(
//                    localMetadataSet.byJotId(remoteUpdate.id())
//                        .stream()
//                        .map(Metadata::title)
//                        .collect(Collectors.joining(","))
//                        .getBytes()
//                )
//            );
//        }
        remoteIndexes.updateMetadata(remoteUpdates);
    }
}
