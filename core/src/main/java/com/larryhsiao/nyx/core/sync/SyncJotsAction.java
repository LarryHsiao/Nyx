package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.jots.moods.JotJson;
import com.larryhsiao.nyx.core.jots.moods.JsonJot;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.util.FactorySource;

import javax.json.Json;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
    private final ExecutorService worker;

    public SyncJotsAction(
        Nyx nyx,
        RemoteFiles remoteFiles,
        NyxIndexes remoteIndexes,
        ExecutorService worker
    ) {
        this.nyx = nyx;
        this.remoteFiles = remoteFiles;
        this.remoteIndexes = remoteIndexes;
        this.worker = worker;
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
        final List<Future<?>> asyncFuture = new ArrayList<>();
        for (JotIndex remoteIndex : remoteJotMap.values()) {
            if (localJotMap.containsKey(remoteIndex.id())) {
                final Jot localJot = localJotMap.get(remoteIndex.id());
                if (remoteIndex.version() > localJot.version()) {
                    asyncFuture.add(worker.submit(() -> nyx.jots().replace(toJot(remoteIndex))));
                } else if (remoteIndex.version() < localJot.version()) {
                    remoteUpdates.add(localJot);
                }
                localJotMap.remove(remoteIndex.id());
            } else {
                asyncFuture.add(worker.submit(() -> nyx.jots().createWithId(toJot(remoteIndex))));
            }
        }
        for (Future<?> future : asyncFuture) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        remoteUpdates.addAll(localJotMap.values()); // New jots at remote
        updateRemotes(remoteUpdates);
    }

    private void updateRemotes(List<Jot> remoteUpdates) {
        final List<Future<?>> asyncFuture = new ArrayList<>();
        for (Jot remoteUpdate : remoteUpdates) {
            if (remoteUpdate.deleted()) {
                asyncFuture.add(
                    // remove jot folder
                    worker.submit(() -> remoteFiles.delete("/" + remoteUpdate.id()))
                );
            } else {
                asyncFuture.add(
                    worker.submit(() -> {
                        remoteFiles.post(
                            String.format(CONTENT_FILE_PATH, remoteUpdate.id() + ""),
                            new FactorySource<>(unused ->
                                new ByteArrayInputStream(
                                    new JotJson(remoteUpdate).value().toString()
                                        .getBytes(StandardCharsets.UTF_8)
                                ))
                        );
                        remoteFiles.post(
                            String.format(TAG_FILE_PATH, remoteUpdate.id() + ""),
                            new FactorySource<>(unused ->
                                new ByteArrayInputStream(
                                    nyx.tags().byJotId(remoteUpdate.id())
                                        .stream()
                                        .map(Tag::title)
                                        .collect(Collectors.joining(","))
                                        .getBytes(StandardCharsets.UTF_8)
                                )
                            )
                        );
                    })
                );
            }
        }
        for (Future<?> future : asyncFuture) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
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
