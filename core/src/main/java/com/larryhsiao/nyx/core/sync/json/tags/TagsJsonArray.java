package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.sync.json.jots.JotJson;
import com.larryhsiao.nyx.core.tags.Tag;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.List;

/**
 * Source to build json array from list of {@link Jot}
 */
public class TagsJsonArray implements Source<JsonArray> {
    private final List<Tag> tags;

    public TagsJsonArray(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public JsonArray value() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Tag tag : tags) {
            arrayBuilder.add(new TagJson(tag).value());
        }
        return arrayBuilder.build();
    }

}
