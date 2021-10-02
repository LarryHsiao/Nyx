package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.moods.JsonJot;
import com.larryhsiao.nyx.core.tags.Tag;

import javax.json.Json;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Action to sync local jot to remote's file base system.
 */
public class SyncJotsAction implements Action {
    private static final String CONTENT_FILE_PATH = "/%s/content.txt";
    private static final String TAG_FILE_PATH = "/%s/tags.txt";
    private final Nyx nyx;
    private final RemoteFiles remoteFiles;
    private final NyxIndexes remoteIndexes;

    public SyncJotsAction(
        Nyx nyx, RemoteFiles remoteFiles, NyxIndexes remoteIndexes) {
        this.nyx = nyx;
        this.remoteFiles = remoteFiles;
        this.remoteIndexes = remoteIndexes;
    }

    @Override
    public void fire() {
        final List<Jot> remoteUpdates = new ArrayList<>();
        final Map<Long, Jot> localJotMap = nyx.jots().all()
            .stream()
            .collect(Collectors.toMap(Jot::id, Function.identity()));
        final Map<Long, JotIndex> remoteJotMap = this.remoteIndexes.jots()
            .stream()
            .collect(Collectors.toMap(JotIndex::id, Function.identity()));
        for (JotIndex remoteIndex : remoteJotMap.values()) {
            if (localJotMap.containsKey(remoteIndex.id())) {
                final Jot localJot = localJotMap.get(remoteIndex.id());
                if (remoteIndex.version() > localJot.version()) {
                    try {
                        nyx.jots().replace(toJot(remoteIndex));
                    } catch (Exception e) { // @todo #101 Failure of jot downloading
                        e.printStackTrace();
                    }
                } else if (remoteIndex.version() < localJot.version()) {
                    remoteUpdates.add(localJot);
                }
                localJotMap.remove(remoteIndex.id());
            } else {
                try {
                    nyx.jots().createWithId(toJot(remoteIndex));
                } catch (Exception e) { // @todo #102 Failure of jot downloading.
                    e.printStackTrace();
                }
            }
        }
        remoteUpdates.addAll(localJotMap.values()); // New jots at remote
        updateRemotes(remoteUpdates);
    }

    private void updateRemotes(List<Jot> remoteUpdates) {
        for (Jot remoteUpdate : remoteUpdates) {
            if (remoteUpdate.deleted()) {
                remoteFiles.delete("/" + remoteUpdate.id());// remove jot folder
            } else {
                remoteFiles.post(
                    String.format(CONTENT_FILE_PATH, remoteUpdate.id() + ""),
                    new ByteArrayInputStream(
                        remoteUpdate.content().getBytes()
                    )
                );
                remoteFiles.post(
                    String.format(TAG_FILE_PATH, remoteUpdate.id() + ""),
                    new ByteArrayInputStream(
                        nyx.tags().byJotId(remoteUpdate.id())
                            .stream()
                            .map(Tag::title)
                            .collect(Collectors.joining(","))
                            .getBytes()
                    )
                );
            }
        }
        remoteIndexes.updateJots(remoteUpdates);
    }

    private Jot toJot(JotIndex remoteJot) {
        return new JsonJot(
            Json.createReader(
                remoteFiles.get(
                    String.format(CONTENT_FILE_PATH, remoteJot.id() + "")
                )
            ).readObject()
        );
    }
}
