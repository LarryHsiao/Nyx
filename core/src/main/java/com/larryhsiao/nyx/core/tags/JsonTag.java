package com.larryhsiao.nyx.core.tags;

import org.jetbrains.annotations.NotNull;

import javax.json.JsonObject;

/**
 * Json implementation of {@link Tag}.
 */
public class JsonTag implements Tag {
    private final JsonObject obj;

    public JsonTag(JsonObject obj) {this.obj = obj;}

    @NotNull
    @Override
    public String title() {
        try {
            return obj.getString("title");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public long id() {
        try {
            return obj.getJsonNumber("id").longValue();
        } catch (Exception e) {
            return -1L;
        }
    }

    @Override
    public int version() {
        try {
            return obj.getInt("version");
        } catch (Exception e) {
            return -1;
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
