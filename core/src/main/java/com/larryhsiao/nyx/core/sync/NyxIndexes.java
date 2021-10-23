package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.metadata.Metadata;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.Tags;

import java.util.List;
import java.util.Map;

/**
 * The Indexes at remote file system.
 */
public interface NyxIndexes {
    List<JotIndex> jots();

    List<Tag> tags();

    List<Attachment> attachments();

    void updateTags(List<Tag> newTags);

    void updateJots(List<Jot> newJots);

    void updateMetadata(List<Metadata> newMetadata);

    void updateAttachments(List<Attachment> newAttachments);

    List<Metadata> metadata();

    void lock();

    boolean isLocked();

    void unlock();
}
