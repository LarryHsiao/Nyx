package com.larryhsiao.nyx.jots;

/**
 * Decorator of Jot for new value of Jot.
 */
public class UpdatedJot implements Jot {
    private final Jot original;
    private final String newContent;

    public UpdatedJot(Jot original, String newContent) {
        this.original = original;
        this.newContent = newContent;
    }

    @Override
    public long id() {
        return original.id();
    }

    @Override
    public String content() {
        return newContent;
    }

    @Override
    public long createdTime() {
        return original.createdTime();
    }
}
