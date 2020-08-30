package com.larryhsiao.nyx.core.jots;

/**
 * Const of Jot
 */
public class ConstJot implements Jot {
    private final long id;
    private final String title;
    private final String content;
    private final long createdTime;
    private final double[] location;
    private final String mood;
    private final int version;
    private final boolean deleted;

    public ConstJot(
        long id,
        String title,
        String content,
        long createdTime,
        double[] location,
        String mood,
        int version,
        boolean deleted
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.location = location;
        this.mood = mood;
        this.version = version;
        this.deleted = deleted;
    }

    public ConstJot(Jot jot) {
        this.id = jot.id();
        this.title = jot.title();
        this.location = jot.location();
        this.createdTime = jot.createdTime();
        this.content = jot.content();
        this.mood = jot.mood();
        this.version = jot.version();
        this.deleted = jot.deleted();
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
    public String content() {
        return content;
    }

    @Override
    public long createdTime() {
        return createdTime;
    }

    @Override
    public double[] location() {
        return location;
    }

    @Override
    public String mood() {
        return mood;
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
