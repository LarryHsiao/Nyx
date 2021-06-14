package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Action;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Action for sync local Nyx to remote file-base system.
 */
public class SyncAction implements Action {
    private final Nyx nyx;
    private final NyxIndexes remoteNyxIndexes;
    private final RemoteFiles remoteFiles;

    public SyncAction(Nyx nyx, NyxIndexes remoteNyxIndexes, RemoteFiles remoteFiles) {
        this.nyx = nyx;
        this.remoteNyxIndexes = remoteNyxIndexes;
        this.remoteFiles = remoteFiles;
    }

    @Override
    public void fire() {
        final List<Jot> all = nyx.jots().all();
        final Map<Long, JotIndex> remoteJot = remoteNyxIndexes.jots()
            .stream()
            .collect(Collectors.toMap(JotIndex::id, Function.identity()));
    }
}
