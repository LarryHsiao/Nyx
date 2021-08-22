package com.larryhsiao.nyx.core.jots.moods;

import com.larryhsiao.nyx.core.jots.Jot;
import org.jetbrains.annotations.NotNull;

import javax.json.JsonObject;

/**
 * Json implementation of {@link Jot}.
 */
public class JsonJot implements Jot {
    private final JsonObject json;

    public JsonJot(JsonObject json) {
        this.json = json;
    }

    @Override
    public long id() {
        return 0;
    }

    @NotNull
    @Override
    public String title() {
        return null;
    }

    @NotNull
    @Override
    public String content() {
        return null;
    }

    @Override
    public long createdTime() {
        return 0;
    }

    @NotNull
    @Override
    public double[] location() {
        return new double[0];
    }

    @NotNull
    @Override
    public String mood() {
        return null;
    }

    @Override
    public int version() {
        return 0;
    }

    @Override
    public boolean privateLock() {
        return false;
    }

    @Override
    public boolean deleted() {
        return false;
    }
}
