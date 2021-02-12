package com.larryhsiao.nyx.core.tags;

/**
 * Wrapped {@link JotTag}.
 */
public class WrappedJotTags implements JotTag{
    private final JotTag original;

    public WrappedJotTags(JotTag original) {
        this.original = original;
    }

    @Override
    public long jotId() {
        return original.jotId();
    }

    @Override
    public long tagId() {
        return original.tagId();
    }

    @Override
    public boolean deleted() {
        return original.deleted();
    }

    @Override
    public int version() {
        return original.version();
    }
}
