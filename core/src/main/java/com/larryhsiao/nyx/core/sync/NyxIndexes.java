package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.Tags;

import java.util.List;

/**
 * The Indexes at remote file system.
 */
public interface NyxIndexes {
    List<JotIndex> jots();

    List<Tag> tags();

    void updateTags(List<Tag> tags);
}
