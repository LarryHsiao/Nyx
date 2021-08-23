package com.larryhsiao.nyx.core.sync;

import java.util.List;

/**
 * Constant implementation of {@link JotIndex}.
 */
public class ConstJotIndex implements JotIndex {
    private final long id;
    private final int version;
    private final boolean delete;
    private final List<Long> tagIds;
    private final List<AttachmentIndex> attachments;
    private final List<MetadataIndex> metadata;

    public ConstJotIndex(
        long id,
        int version,
        boolean delete,
        List<Long> tagIds,
        List<AttachmentIndex> attachments,
        List<MetadataIndex> metadata
    ) {
        this.id = id;
        this.version = version;
        this.delete = delete;
        this.tagIds = tagIds;
        this.attachments = attachments;
        this.metadata = metadata;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public boolean deleted() {
        return delete;
    }

    @Override
    public List<Long> tagIds() {
        return tagIds;
    }

    @Override
    public List<AttachmentIndex> attachments() {
        return attachments;
    }

    @Override
    public List<MetadataIndex> metadata() {
        return metadata;
    }
}
