package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.tags.JotTag;
import com.larryhsiao.nyx.core.tags.Tag;

import java.util.Map;
import java.util.function.Function;

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
        syncJotTags();
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

    private void syncJotTags() {
        final Map<String, JotTag> tags1 = nyx1.jotTags()
            .all()
            .stream()
            .collect(toMap(
                jotTag -> jotTag.jotId() + " " + jotTag.tagId(),
                tag -> tag
            ));
        final Map<String, JotTag> tags2 = nyx2.jotTags()
            .all()
            .stream()
            .collect(toMap(
                jotTag -> jotTag.jotId() + " " + jotTag.tagId(),
                tag -> tag
            ));
        for (JotTag tag1 : tags1.values()) {
            JotTag tag2 = tags2.get(tag1.jotId() + " " + tag1.tagId());
            if (tag2 == null) {
                nyx2.jotTags().link(tag1.jotId(), tag1.tagId());
            } else {
                if (tag1.version() > tag2.version()) {
                    nyx2.jotTags().update(tag1);
                } else if (tag1.version() < tag2.version()) {
                    nyx1.jotTags().update(tag2);
                }
                tags2.remove(tag1.jotId() + " " + tag1.tagId());
            }
        }
        // Adding new JotTags from second one
        for (JotTag tag2 : tags2.values()) {
            nyx1.jotTags().link(tag2.jotId(), tag2.tagId());
        }
    }

    private void syncTags() {
        final Map<Long, Tag> tags1 = nyx1.tags()
            .all()
            .stream()
            .collect(toMap(Tag::id, tag -> tag));
        final Map<Long, Tag> tags2 = nyx2.tags()
            .all()
            .stream()
            .collect(toMap(Tag::id, tag -> tag));
        for (Tag tag1 : tags1.values()) {
            Tag tag2 = tags2.get(tag1.id());
            if (tag2 == null) {
                nyx2.tags().create(tag1);
            } else {
                if (tag1.version() > tag2.version()) {
                    nyx2.tags().update(tag1);
                } else if (tag1.version() < tag2.version()) {
                    nyx1.tags().update(tag2);
                }
                tags2.remove(tag1.id());
            }
        }
        // Adding new Jots from second one
        for (Tag Tag2 : tags2.values()) {
            nyx1.tags().create(Tag2);
        }
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
