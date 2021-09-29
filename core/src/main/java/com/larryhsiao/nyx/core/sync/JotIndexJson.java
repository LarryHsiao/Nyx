package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.Source;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.List;

public class JotIndexJson implements Source<JsonObject> {
    private final JotIndex jotIndex;

    public JotIndexJson(JotIndex jotIndex) {this.jotIndex = jotIndex;}

    @Override
    public JsonObject value() {
        return Json.createObjectBuilder()
            .add("id", jotIndex.id())
            .add("version", jotIndex.version())
            .add("deleted", jotIndex.deleted())
            .add("tagIds", tagIds())
            .build();
    }

    private JsonArray tagIds(){
        final List<Long> tagIds = jotIndex.tagIds();
        final JsonArrayBuilder tagIdArray = Json.createArrayBuilder();
        for (Long tagId : tagIds) {
            tagIdArray.add(tagId);
        }
        return tagIdArray.build();
    }
}
