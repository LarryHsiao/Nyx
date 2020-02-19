package com.larryhsiao.nyx.jots;

import com.silverhetch.clotho.Source;

/**
 * Decorator of Jot for new value of Jot.
 */
public class UpdatedJot implements Jot {
    private final Jot original;
    private final String newContent;
    private final Source<double[]> location;

    public UpdatedJot(Jot original, String newContent) {
        this(original, newContent, null);
    }

    public UpdatedJot(Jot original, String newContent, Source<double[]> location) {
        this.original = original;
        this.newContent = newContent;
        this.location = location;
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

    @Override
    public double[] location() {
        if (location == null) {
            return original.location();
        } else {
            return location.value();
        }
    }
}
