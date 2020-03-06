package com.larryhsiao.nyx.core.tags;

/**
 * Constant of Tag
 */
public class ConstTag implements Tag {
    private final long id;
    private final String title;

    public ConstTag(long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public long id() {
        return id;
    }
}
