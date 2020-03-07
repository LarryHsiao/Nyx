package com.larryhsiao.nyx.core.tags;

/**
 * Constant of Tag
 */
public class ConstTag implements Tag {
    private final long id;
    private final String title;
    private final int version;

    public ConstTag(long id, String title, int version) {
        this.id = id;
        this.title = title;
        this.version = version;
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
}
