package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.nyx.core.tags.Tag;
import org.jetbrains.annotations.NotNull;

import javax.json.JsonObject;

/**
 * Json implementation of {@link Tag}.
 */
public class JsonTag implements Tag {
    private final JsonObject object;

    public JsonTag(JsonObject object) {
        this.object = object;
    }

    @NotNull
    @Override
    public String title() {
        try {
            return object.getString("title");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public long id() {
        try {
            return object.getJsonNumber("id").longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public int version() {
        try {
            return object.getInt("version");
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean deleted() {
        try {
            return object.getBoolean("delete");
        } catch (Exception e) {
            return false;
        }
    }
}
