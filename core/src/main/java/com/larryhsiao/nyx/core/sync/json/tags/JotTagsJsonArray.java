package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.tags.JotTag;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.List;

/**
 * Source to build a JsonArray from given Jot Tag.
 */
public class JotTagsJsonArray implements Source<JsonArray> {
    private final List<JotTag> jotTags;

    public JotTagsJsonArray(List<JotTag> jotTags) {
        this.jotTags = jotTags;
    }

    @Override
    public JsonArray value() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (JotTag jotTag : jotTags) {
            arrayBuilder.add(new JotTagJson(jotTag).value());
        }
        return arrayBuilder.build();
    }
}
