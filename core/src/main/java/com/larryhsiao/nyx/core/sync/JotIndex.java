package com.larryhsiao.nyx.core.sync;

import java.util.List;

/**
 * The Jot index at remote file storage.
 */
public interface JotIndex {
    long id();

    int version();

    boolean deleted();

    List<Long> tagIds(); // Data link only, the tags will be sync independently. Actual jot file will have the tag name but missing is okay.

    List<AttachmentIndex> attachments(); // The attachment is one to one structure, so we just put it here rather than root index.

    List<MetadataIndex> metadata(); // The metadata is one to one structure, so we just put it here rather than root index.
}
