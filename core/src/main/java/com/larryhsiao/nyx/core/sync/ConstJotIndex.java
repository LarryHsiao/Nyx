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

    public ConstJotIndex(
        long id,
        int version,
        boolean delete,
        List<Long> tagIds
    ) {
        this.id = id;
        this.version = version;
        this.delete = delete;
        this.tagIds = tagIds;
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
}
