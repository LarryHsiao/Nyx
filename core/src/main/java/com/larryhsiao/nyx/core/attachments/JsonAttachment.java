package com.larryhsiao.nyx.core.attachments;

import org.jetbrains.annotations.NotNull;

import javax.json.JsonObject;

public class JsonAttachment implements Attachment {
    private final JsonObject jsonObject;

    public JsonAttachment(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public long id() {
        try {
            return jsonObject.getJsonNumber("id").longValue();
        } catch (Exception e) {
            return -1L;
        }
    }

    @Override
    public long jotId() {
        try {
            return jsonObject.getJsonNumber("jotId").longValue();
        } catch (Exception e) {
            return -1L;
        }
    }

    @NotNull
    @Override
    public String uri() {
        try {
            return jsonObject.getString("uri");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int version() {
        try {
            return jsonObject.getInt("version");
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean deleted() {
        try {
            return jsonObject.getBoolean("deleted");
        } catch (Exception e) {
            return false;
        }
    }
}
