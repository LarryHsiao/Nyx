package com.larryhsiao.nyx.core.tags;

/**
 * Constant of Tag
 */
public class ConstTag implements Tag {
    private final long id;
    private final String title;
    private final int version;
    private final boolean deleted;

    public ConstTag(long id, String title, int version, boolean deleted) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.deleted = deleted;
    }

    @Override
    public String title() {
        return title;
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
        return deleted;
    }
}
