package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jot;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Object to sync data to given remote host.
 */
public class NyxSync {
    private final Nyx localNyx;
    private final Nyx remoteNyx;
    private boolean running = false;

    public NyxSync(Nyx localNyx, Nyx remoteNyx) {
        this.localNyx = localNyx;
        this.remoteNyx = remoteNyx;
    }

    public void sync() {
        if (running) {
            return;
        }
        running = true;
        syncJots(); // @todo #107 Jot sync
        syncTags(); // @todo #108 Tag sync
        // @todo #109 Attachment sync
        // @todo #110 Metadata sync
        // @todo #111 File sync
    }

    private void syncTags() {
    }

    private void syncJots() {
        final Map<Long, Jot> localJots = localNyx.jots()
            .all().stream()
            .collect(Collectors.toMap(Jot::id, jot -> jot));
        final Map<Long, Jot> remoteJots = remoteNyx.jots()
            .all().stream()
            .collect(Collectors.toMap(Jot::id, jot -> jot));
        for (Map.Entry<Long, Jot> entry : localJots.entrySet()) {
            Jot local = entry.getValue();
        }
    }
}
