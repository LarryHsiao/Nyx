package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.nyx.core.tags.JotTag;

import javax.json.JsonObject;

/**
 * Json implementation of {@link JotTag}.
 */
public class JsonJotTag implements JotTag {
    private final JsonObject jsonObj;

    public JsonJotTag(JsonObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    @Override
    public long jotId() {
        try {
            return jsonObj.getJsonNumber("jot_id").longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public long tagId() {
        try {
            return jsonObj.getJsonNumber("tag_id").longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean deleted() {
        try {
            return jsonObj.getBoolean("deleted");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int version() {
        try {
            return jsonObj.getInt("version");
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
