package com.larryhsiao.nyx.core.tags;

import com.larryhsiao.clotho.Source;

import javax.json.Json;
import javax.json.JsonObject;

public class TagJson implements Source<JsonObject> {
    private final Tag tag;

    public TagJson(Tag tag) {this.tag = tag;}

    @Override
    public JsonObject value() {
        return Json.createObjectBuilder()
            .add("id", tag.id())
            .add("title", tag.title())
            .add("version", tag.version())
            .add("deleted", tag.deleted())
            .build();
    }
}
