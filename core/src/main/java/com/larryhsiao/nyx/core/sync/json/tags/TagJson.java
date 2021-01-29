package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.tags.Tag;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Source to build {@link JsonObject} from given {@link Tag}.
 */
public class TagJson implements Source<JsonObject> {
    private final Tag tag;

    public TagJson(Tag tag) {
        this.tag = tag;
    }

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
