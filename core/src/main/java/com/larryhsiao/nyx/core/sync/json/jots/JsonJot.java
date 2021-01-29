package com.larryhsiao.nyx.core.sync.json.jots;

import com.larryhsiao.nyx.core.jots.Jot;
import org.jetbrains.annotations.NotNull;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Json implementation of Jot.
 * Typically, the json is returned by another Nyx Server.
 */
public class JsonJot implements Jot {
    private final JsonObject obj;

    public JsonJot(JsonObject obj) {
        this.obj = obj;
    }

    @Override
    public long id() {
        try {
            return obj.getJsonNumber("id").longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @NotNull
    @Override
    public String title() {
        try {
            return obj.getString("title");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @NotNull
    @Override
    public String content() {
        try {
            return obj.getString("content");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public long createdTime() {
        try {
            return obj.getJsonNumber("createdTime").longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @NotNull
    @Override
    public double[] location() {
        try {
            final double[] result = new double[2];
            final JsonArray location = obj.getJsonArray("location");
            for (int i = 0; i < 2; i++) {
                result[i] = location.getJsonNumber(i).doubleValue();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        }
    }

    @NotNull
    @Override
    public String mood() {
        try {
            return obj.getString("mood");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int version() {
        try {
            return obj.getInt("version");
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean privateLock() {
        try {
            return obj.getBoolean("privateLock");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleted() {
        try {
            return obj.getBoolean("deleted");
        } catch (Exception e) {
            return false;
        }
    }
}
