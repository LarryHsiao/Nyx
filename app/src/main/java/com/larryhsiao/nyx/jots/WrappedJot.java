package com.larryhsiao.nyx.jots;

/**
 * Wrap object of Jot
 */
public class WrappedJot implements Jot {
    private final Jot jot;

    public WrappedJot(Jot jot) {
        this.jot = jot;
    }

    @Override
    public long id() {
        return jot.id();
    }

    @Override
    public String content() {
        return jot.content();
    }

    @Override
    public long createdTime() {
        return jot.createdTime();
    }

    @Override
    public String mood() {
        return jot.mood();
    }

    @Override
    public double[] location() {
        return jot.location();
    }
}

