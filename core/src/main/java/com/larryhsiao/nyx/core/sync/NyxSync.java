package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jot;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Object to sync data of two {@link Nyx} instance.
 */
public class NyxSync {
    private final Nyx nyx1;
    private final Nyx nyx2;
    private boolean running = false;

    public NyxSync(Nyx nyx1, Nyx nyx2) {
        this.nyx1 = nyx1;
        this.nyx2 = nyx2;
    }

    public void sync() {
        if (running) {
            return;
        }
        running = true;
        syncJots();
        syncTags();
        syncAttachment();
        syncMetadata();
        syncFile();
    }

    private void syncFile() {
        // @todo #111 File sync
    }

    private void syncMetadata() {
        // @todo #110 Metadata sync
    }

    private void syncAttachment() {
        // @todo #109 Attachment sync
    }

    private void syncTags() {
        // @todo #108 Tag sync
    }

    private void syncJots() {
        final Map<Long, Jot> jots1 = nyx1.jots()
            .all()
            .stream()
            .collect(toMap(Jot::id, jot -> jot));
        final Map<Long, Jot> jots2 = nyx2.jots()
            .all()
            .stream()
            .collect(toMap(Jot::id, jot -> jot));
        for (Jot jot1 : jots1.values()) {
            Jot jot2 = jots2.get(jot1.id());
            if (jot2 == null) {
                nyx2.jots().create(jot1);
            } else {
                if (jot1.version() > jot2.version()) {
                    nyx2.jots().update(jot1);
                } else if (jot1.version() < jot2.version()) {
                    nyx1.jots().update(jot2);
                }
                jots2.remove(jot1.id());
            }
        }
        // Adding new Jots from second one
        for (Jot jot2 : jots2.values()) {
            nyx1.jots().create(jot2);
        }
    }
}
