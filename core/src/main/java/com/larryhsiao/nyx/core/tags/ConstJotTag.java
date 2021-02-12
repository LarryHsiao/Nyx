package com.larryhsiao.nyx.core.tags;

/**
 * Constant {@link JotTag}.
 */
public class ConstJotTag implements JotTag {
    private final long jotId;
    private final long tagId;
    private final boolean deleted;
    private final int version;

    public ConstJotTag(
        long jotId,
        long tagId,
        boolean deleted,
        int version
    ) {
        this.jotId = jotId;
        this.tagId = tagId;
        this.deleted = deleted;
        this.version = version;
    }

    @Override
    public long jotId() {
        return jotId;
    }

    @Override
    public long tagId() {
        return tagId;
    }

    @Override
    public boolean deleted() {
        return deleted;
    }

    @Override
    public int version() {
        return version;
    }
}
