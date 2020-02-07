package com.larryhsiao.nyx.jots;

/**
 * Const of Jot
 */
public class ConstJot implements Jot {
    private final long id;
    private final String content;
    private final long createdTime;

    public ConstJot(long id, String content, long createdTime) {
        this.id = id;
        this.content = content;
        this.createdTime = createdTime;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public long createdTime() {
        return createdTime;
    }
}
