package com.larryhsiao.nyx.jots;


import com.silverhetch.clotho.Source;
import com.silverhetch.clotho.source.ConstSource;

/**
 * Const of Jot
 */
public class ConstJot implements Jot {
    private final long id;
    private final String content;
    private final long createdTime;
    private final double[] location;

    public ConstJot(long id, String content, long createdTime, double[] location) {
        this.id = id;
        this.content = content;
        this.createdTime = createdTime;
        this.location = location;
    }

    public ConstJot(Jot jot) {
        this.id = jot.id();
        this.location = jot.location();
        this.createdTime = jot.createdTime();
        this.content = jot.content();
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
}
