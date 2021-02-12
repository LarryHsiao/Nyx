package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.tags.JotTag;

import javax.json.Json;
import javax.json.JsonObject;

public class JotTagJson implements Source<JsonObject> {
    private final JotTag jotTag;

    public JotTagJson(JotTag jotTag) {
        this.jotTag = jotTag;
    }

    @Override
    public JsonObject value() {
        return Json.createObjectBuilder()
            .add("jot_id", jotTag.jotId())
            .add("tag_id", jotTag.tagId())
            .add("version", jotTag.version())
            .add("deleted", jotTag.deleted())
            .build();
    }
}
