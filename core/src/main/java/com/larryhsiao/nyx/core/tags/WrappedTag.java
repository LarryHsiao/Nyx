package com.larryhsiao.nyx.core.tags;

/**
 * Wrapper class of Tag
 */
public class WrappedTag implements Tag {
    private final Tag origin;

    public WrappedTag(Tag origin) {
        this.origin = origin;
    }

    @Override
    public String title() {
        return origin.title();
    }

    @Override
    public long id() {
        return origin.id();
    }

    @Override
    public int version() {
        return origin.version();
    }

    @Override
    public boolean deleted() {
        return origin.deleted();
    }
}
